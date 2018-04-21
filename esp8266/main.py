import machine
import mfrc522
# import ssd1306
import time
import ustruct

import network
import usocket
import ADXL345

#Configure Rfid
#sck, mosi, miso, rst, cs
rdr = mfrc522.MFRC522(14, 13, 12, 16, 0)

#Configure bluetooth uart
uart = machine.UART(1, 9600)                         # init with given baudrate
uart.init(9600, bits=8, parity=None, stop=1) # init with given parameters

#wake up accelerometer to power mode
i2c = machine.I2C(scl=machine.Pin(5), sda=machine.Pin(4),freq=400000)
accelerometer = ADXL345.ADXL345(i2c)

count=0 
while True:    
    count=count+1

    if(count==5):
        count=0
        #accelerometer
        x,y,z=accelerometer.Value()
        # print("acc,%d,%d,%d"%(x,y,z))
        uart.write("acc,%d,%d,%d"%(x,y,z)) 

    (stat, tag_type) = rdr.request(rdr.REQIDL)

    if stat == rdr.OK:

        (stat, raw_uid) = rdr.anticoll()

        if stat == rdr.OK:
            # print(type(raw_uid))
            # print("New card detected")
            # print("  - tag type: 0x%02x" % tag_type)
            print("rfid: 0x%02x%02x%02x%02x" % (raw_uid[0], raw_uid[1], raw_uid[2], raw_uid[3]))
            uart.write("rfid:0x%02x%02x%02x%02x" % (raw_uid[0], raw_uid[1], raw_uid[2], raw_uid[3]))
