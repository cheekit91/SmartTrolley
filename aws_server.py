#newapp1.py

from flask import Flask, render_template
from flask import jsonify
from flask import request
from flask_pymongo import PyMongo
import json
import numpy as np
import pickle
import aws

app= Flask(__name__)

app.config['MONGO_DBNAME']= 'smarttrolley'
app.config['MONGO_URL']= 'mongodb://localhost:27017/smarttrolley'
mongo= PyMongo(app)

@app.route('/', methods=['POST'])
def query_database():
    print( "Hello world")
    print(request.json["type"])
    print(request.json["msg"])
    
    #Command Type- getHelp- Purpose: To send sms to local assistant.
    if(request.json["type"]=="getHelp"):
      response = snsClient.publish(
          TopicArn = 'Removed as sensitive address here',
    Message = "Trolley %s need help!"%request.json["msg"],
          Subject ='Need help in store'
      )
      return "done"

    #Command Type- addToBuyList- Purpose: To add item to ToBuyList (if item exists, update quantity. Else, insert data)
    if(request.json["type"]=="addToBuyList"):
      method=request.json["msg"]["method"] #0- recipe #1-user item #2-database item
      itemname=request.json["msg"]["itemname"]
      quantity=request.json["msg"]["quantity"]
      databaseitems=mongo.db.items
      toBuyList = mongo.db.toBuyList
      #add from recipe (multiple items)
      if(method=='0'):
        recipes = mongo.db.recipes
        itemlist=recipes.find({'recipename':itemname})
        for i in itemlist:
          j=databaseitems.find({'itemname':i['itemname']})[0]
          checkToBuyList=toBuyList.find({'itemname':i['itemname']})
          if(checkToBuyList.count()==0):
            toBuyList.insert({'userid':request.json['userid'],'itemname':i['itemname'],'tagid':j['tagid'],'quantity':i['quantity'],'price':j['price'],'cat':j['cat'],'location':j['location']})
          else:
            i['quantity']=str(int(i['quantity'])+int(checkToBuyList[0]['quantity']))
            toBuyList.update({'itemname':i['itemname'],'userid':request.json['userid']},{'userid':request.json['userid'],'itemname':i['itemname'],'tagid':j['tagid'],'quantity':i['quantity'],'price':j['price'],'cat':j['cat'],'location':j['location']})
      #add from favorite item/database (single item)
      if(method=='1' or method=='2'):
        j=databaseitems.find({'itemname':itemname})[0]
        checkToBuyList=toBuyList.find({'itemname':itemname})
        if(checkToBuyList.count()==0):
          toBuyList.insert({'userid':request.json['userid'],'itemname':itemname,'tagid':j['tagid'],'quantity':quantity,'price':j['price'],'cat':j['cat'],'location':j['location']})
        else:
          quantity=str(int(quantity)+int(checkToBuyList[0]['quantity']))
          toBuyList.update({'itemname':itemname,'userid':request.json['userid']},{'userid':request.json['userid'],'itemname':itemname,'tagid':j['tagid'],'quantity':quantity,'price':j['price'],'cat':j['cat'],'location':j['location']})
      return "done"
    #Command Type- getToBuyList- Purpose: To retrieve ToBuyList and return data in csv format
    if(request.json["type"]=="getToBuyList"):
      toBuyListItems = mongo.db.toBuyList.find().sort([("location", 1)])
      itemstring=''
      for i in toBuyListItems:
        itemstring+=i['itemname']+','+i['quantity']+','+i['price']+','+i['location']+','
      return itemstring
    #Command Type- removeToBuyList- Purpose: To remove item from ToBuyList
    if(request.json["type"]=="removeToBuyList"):
      mongo.db.toBuyList.remove({'itemname':request.json["msg"],'userid':request.json['userid']})
      return "done"

    #Command Type- moveToPurchaseHistory- Purpose: To move item from ToBuyList to PurchaseHistory
    if(request.json["type"]=="moveToPurchaseHistory"):
      msg=request.json["msg"].split(',')
      tagId=msg[2]
      date=msg[1]
      month=msg[0]
      item = mongo.db.toBuyList.find({"tagid":tagId})
      purchaseHistory=mongo.db.purchaseHistory
      #if tagid match toBuyList Item:
      if(item.count()!=0):
        j=item[0]
        if(j["quantity"]=='1'): #if left with 1 item remove, else update quantity
          mongo.db.toBuyList.remove({"tagid":tagId})
        else:
          quantity=str(int(j['quantity'])-1)
          mongo.db.toBuyList.update({'tagid':tagId,'userid':request.json['userid']},{'tagid':tagId,'userid':request.json['userid'],'itemname':j['itemname'],'quantity':quantity,'price':j['price'],'cat':j['cat'],'location':j['location']})
        
        #find purchaseHistory record, if not exist, insert new record. If exist on same date, insert 
        record=purchaseHistory.find({"tagid":tagId,'userid':request.json['userid'],'date':date,'month':month})
        if(record.count()==0):
          purchaseHistory.insert({'tagid':tagId,'userid':request.json['userid'],'date':date,'month':month,'itemname':j['itemname'],'quantity':'1','price':j['price'],'cat':j['cat'],'location':j['location']})
        else:
          quantity=str(int(record[0]['quantity'])+1)
          purchaseHistory.update({'tagid':tagId,'userid':request.json['userid'],'date':date,'month':month},{'tagid':tagId,'userid':request.json['userid'],'date':date,'month':month,'itemname':j['itemname'],'quantity':quantity,'price':j['price'],'cat':j['cat'],'location':j['location']})
      return "done"

    #Command Type- retrievecat- Purpose: To retrieve unique item categories
    if(request.json["type"]=="retrievecat"):
      databaseitems=mongo.db.items
      catlist=databaseitems.distinct("cat")
      catstring=""
      print(catlist)
      for i in catlist:
        catstring+=i+','
      return catstring
    #Command Type- retrieveitemfromcat- Purpose: To retrieve items in a specific unique item category
    if(request.json["type"]=="retrieveitemfromcat"):
      databaseitems=mongo.db.items
      itemstring=""
      itemlist=databaseitems.find({'cat':request.json["msg"]})
      for i in itemlist:
        itemstring+=i['itemname']+','
      return itemstring
    
    #Command Type- getdatabaseitem- Purpose: To get all items from database
    if(request.json["type"]=="getdatabaseitem"):
      databaseitems=mongo.db.items
      itemstring=""
      itemlist=databaseitems.find()
      for i in itemlist:
        itemstring+=i['itemname']+','
      return itemstring

    #Command Type- additem- Purpose: To add item to user favorite list
    if(request.json["type"]=="additem"):
      items = mongo.db.useritems
      # databaseitems=mongo.db.items
      # itemlist=databaseitems.find({'itemname':request.json["msg"]})
      items.insert({'userid':request.json['userid'],'itemname':request.json["msg"]})
      return "done"
    #Command Type- getitem- Purpose: To get item from user favorite list
    if(request.json["type"]=="getitem"):
      items = mongo.db.useritems
      itemstring=""
      databaseitems=mongo.db.items
      itemlist=items.find({'userid':request.json['userid']})
      for i in itemlist:
        j=databaseitems.find({'itemname':i['itemname']})
        itemstring+=i['itemname']+','+j[0]['price']+','
      return itemstring
    #Command Type- removeitem- Purpose: To remove item from user favorite list
    if(request.json["type"]=="removeitem"):
      items=mongo.db.useritems
      items.remove({'itemname':request.json["msg"],'userid':request.json['userid']})
      return "done"
      
    #Command Type- linkrecipe- Purpose: To create recipe with a list of items
    if(request.json["type"]=="linkrecipe"):
      recipes = mongo.db.recipes
      recipename =  request.json["msg"]["recipename"]
      itemnames = request.json["msg"]["itemname"]
      quantity = request.json["msg"]["quantity"]
      numbers = quantity.split(',')
      items = itemnames.split(',')
      for i in range(len(items)-1):
        print(recipename,items[i])
        recipes.insert({'userid':request.json['userid'],'recipename':recipename,'itemname':items[i],'quantity':numbers[i]})
      return "done"
    #Command Type- getrecipe- Purpose: To retrieve recipe
    if(request.json["type"]=="getrecipe"):
      recipes = mongo.db.recipes
      recipestring=""
      recipelist=recipes.distinct("recipename")
      print(recipelist)
      for i in recipelist:
        j=recipes.find({"recipename":i})
        recipestring+=i+','+str(j.count())+','
      return recipestring
    #Command Type- removerecipe- Purpose: To remove recipe
    if(request.json["type"]=="removerecipe"):
      recipes = mongo.db.recipes
      recipes.remove({'recipename':request.json["msg"],'userid':request.json['userid']})
      return "done"
      
    #Command Type- getuser- Purpose: To retrieve recipe
    if(request.json["type"]=="getuser"):
      user = mongo.db.user
      userDataString=""
      userlist=user.find()
      for i in userlist:
        userDataString+=i['user']+','+i['password']+','+i['userid']+';'
      # print(userDataString)
      return userDataString

    
    #Command Type- gettrolley- Purpose: To get trolley item
    if(request.json["type"]=="gettrolley"):
      trolleyinfo = mongo.db.trolleyinfo
      items=trolleyinfo.find({})
      itemString=""
      for item in items:
        itemString+=item["itemname"]+","
      return itemString  
      
    #Command Type- recipesitem- Purpose: To get recipe items
    if(request.json["type"]=="recipesitem"):
        recipes = mongo.db.recipes
        recipelist=recipes.find({'recipename':request.json["msg"]})
        recipesitem_string = ""
        for r in recipelist:
            recipesitem_string += r['itemname']+','
            print(r['itemname'])
        return recipesitem_string
        
    
    #Command Type- getPurchaseHistoryPieChart- Purpose: To get Purchase History Categories to populate into Pie Chart
    if(request.json["type"]=="getPurchaseHistoryPieChart"):
        category = {}
        purchaseHistory = mongo.db.purchaseHistory
        purchaseHistoryList = purchaseHistory.find()
        for item in purchaseHistoryList:
            if item['cat'] not in category:
                category[item['cat']] = float(item['price'])*int(item['quantity'])
            else:
                category[item['cat']] += float(item['price'])*int(item['quantity'])
        category_string = ""
        for i in category:
            category_string += i + ',' + str(category[i]) + ';'
        print category_string
        return category_string

    #Command Type- getPurchaseHistoryBarChart- Purpose: To get Monthly Purchase History to populate into Bar Chart
    if(request.json["type"]=="getPurchaseHistoryBarChart"):
        month={'1':0,'2':0,'3':0,'4':0,'5':0,'6':0,'7':0,'8':0,'9':0,'10':0,'11':0,'12':0}
        purchaseHistory = mongo.db.purchaseHistory
        purchaseHistoryList = purchaseHistory.find()
        for item in purchaseHistoryList:
            month[item['month']] += float(item['price'])*int(item['quantity'])
        month_string = ""
        for i in range(12):
            month_string += str(i+1) + ',' + str(month[str(i+1)]) + ';'
        print month_string
        return month_string
    
    #Command Type- getPurchaseHistory- Purpose: To get a summary of Purchase History in a specific month
    if(request.json["type"]=="getPurchaseHistory"):
        uniquequantity = {}
        uniqueprices ={}
        uniquenames =[]
        purchaseHistory = mongo.db.purchaseHistory
        purchaseHistoryList = purchaseHistory.find({'month':request.json["msg"]})
        month_string=""
        for item in purchaseHistoryList:
            if item['itemname'] not in uniquequantity:
              uniquenames.append(item['itemname'])
              uniquequantity[item['itemname']]=int(item['quantity'])
              uniqueprices[item['itemname']]=item['price']
            else:
              uniquequantity[item['itemname']]+=int(item['quantity'])
        for i in uniquenames:
            month_string += str(uniquequantity[i])+'X $'+uniqueprices[i] + ',' + i + ';'
        print month_string
        return month_string
    
    return "done"

if __name__== '__main__':
  snsClient = aws.getClient('sns','us-east-1')
  app.run(debug=True, host="0.0.0.0", port= 80)



