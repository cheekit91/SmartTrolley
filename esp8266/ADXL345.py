from machine import Pin,I2C

device = const(0x53)
regAddress = const(0x32)
TO_READ = 6
buff = bytearray(6)

class ADXL345:
    def __init__(self,i2c,addr=device):
        self.addr = addr
        self.i2c = i2c
        b = bytearray(1)
        b[0] = 0
        self.i2c.writeto_mem(self.addr,0x2d,b)
        b[0] = 16
        self.i2c.writeto_mem(self.addr,0x2d,b)
        b[0] = 8
        self.i2c.writeto_mem(self.addr,0x2d,b)

    # @property
    def setInterrupt(self):
        b = bytearray(1)
        b[0] = 7
        self.i2c.writeto_mem(self.addr,0x2a,b) #3 axis
        b[0] = 50
        self.i2c.writeto_mem(self.addr,0x1d,b) #tapthreshold
        b[0] = 15
        self.i2c.writeto_mem(self.addr,0x21,b) #duration
        b[0] = 64
        self.i2c.writeto_mem(self.addr,0x2E,b) #interruptenable

    # @property
    def Value(self):
        buff = self.i2c.readfrom_mem(self.addr,regAddress,TO_READ)
        x = (int(buff[1]) << 8) | buff[0]
        if x > 32767:
            x -= 65536
        y = (int(buff[3]) << 8) | buff[2]
        if y > 32767:
            y -= 65536
        z = (int(buff[5]) << 8) | buff[4]
        if z > 32767:
            z -= 65536
        return x,y,z
