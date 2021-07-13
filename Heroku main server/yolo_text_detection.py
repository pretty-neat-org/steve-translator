import cv2
import numpy as np
import pytesseract
import copy
import requests
import json
#from transformers import MarianTokenizer, AutoModelForSeq2SeqLM
# Ucitavanje Yolo. Ubaciti tezine i konfiguracioni fajl u folder gde se nalazi i skripta
#net = cv2.dnn.readNet(r"D:\1 FAKULTET\Master\NM\steve-translator\yolo_custom_detection/yolov3_training_2000.weights", r"D:\1 FAKULTET\Master\NM\steve-translator\yolo_custom_detection/yolov3_testing.cfg")

net = cv2.dnn.readNet(r"yolov3_training_2000.weights", r"yolov3_testing.cfg")
layer_names = net.getLayerNames()
output_layers = [layer_names[i[0] - 1] for i in net.getUnconnectedOutLayers()]

# Name custom object
classes = ["text"]




# prima open_cv image
def find_text_areas(img, source):


    colors = [[0, 0, 255]]
    # custom_config = r'--oem 3 --psm 6'
    # pytesseract.image_to_string(img, config=custom_config)
    # text = pytesseract.image_to_string(img)
    # print(text)
    # return

    img = cv2.resize(img, None, fx=0.4, fy=0.4)

    # dodajem okvir oko slike kako bih mogao da prosirim region za tekst

    ht, wd, cc = img.shape
    # create new image of desired size and color (blue) for padding
    ww = wd + 30
    hh = ht + 30
    color = (0,255,0)
    result = np.full((hh,ww,cc), color, dtype=np.uint8)

    # compute center offset
    xx = (ww - wd) // 2
    yy = (hh - ht) // 2

    # copy img image into center of result image
    result[yy:yy+ht, xx:xx+wd] = img
    img = result
    del result
    #ret_img = copy.copy(img)

    height, width, channels = img.shape


    # Detecting objects
    blob = cv2.dnn.blobFromImage(img, 0.00392, (416, 416), (0, 0, 0), True, crop=False)

    net.setInput(blob)
    outs = net.forward(output_layers)
    #net
    

    # Import model for translation
    #model_base_path = r'translation-models'
    #tokenizer = MarianTokenizer.from_pretrained(model_base_path + r'/it')
    #model = AutoModelForSeq2SeqLM.from_pretrained(model_base_path + r'/it')

    # Pravljenje pravougaonika
    # Showing informations on the screen
    class_ids = []
    confidences = []
    boxes = []
    for out in outs:
        for detection in out:
            scores = detection[5:]
            class_id = np.argmax(scores)
            confidence = scores[class_id]
            if confidence > 0.3:
                # Object detected
                #print(class_id)
                center_x = int(detection[0] * width)
                center_y = int(detection[1] * height)
                w = int(detection[2] * width)
                h = int(detection[3] * height)

                # Rectangle coordinates
                x = int(center_x - w / 2)
                y = int(center_y - h / 2)

                boxes.append([x, y, w, h])
                confidences.append(float(confidence))
                class_ids.append(class_id)

    indexes = cv2.dnn.NMSBoxes(boxes, confidences, 0.5, 0.4)
    #print(indexes)
    #font = cv2.FONT_HERSHEY_PLAIN

    boxes_text = []

    ret_txt = ""

    

    for i in range(len(boxes)):
        if i in indexes:
            x, y, w, h = boxes[i]
            crop_img = img[y - 10:y + h + 10, x - 5 :x + w + 5]

            text = pytesseract.image_to_string(crop_img).replace('?', '')

            # Izbacujem noise
            text = ''.join(filter(str.isalnum, text))
            text = text.replace('\n','')
            text = text.replace('\x0c','')
            
            if text == "":
                text = " "
            
            koordinate_txt = str(x) + "@" + str(y) + "@" + str(w) + "@" + str(h) + "@" + text
            ret_txt = ret_txt + koordinate_txt + ","
        
            #boxes_text.append([x, y, w, h, text])
            #cv2.imshow("Crop image", crop_img)
            #key = cv2.waitKey(0)

            #print(text)
            #label = str(classes[class_ids[i]])
            #color = colors[class_ids[i]]


            #cv2.rectangle(ret_img, (x - 5, y-10), (x + w + 5, y + h + 10), [255,255,255], -1)
            #centar_x = int(w/5)
            #centar_y = int((h+5)/2)
            #cv2.putText(ret_img, text, (x + centar_x, y + centar_y), font, 1, colors[0], 1)

    del img
    if(source == 'just-find-text'):
        return ret_txt[:-1]

    else:    
        return prevedi(ret_txt[:-1], source)

    # cv2.imwrite('output.jpg', ret_img)
    # cv2.imshow("Image", ret_img)
    # key = cv2.waitKey(0)

def prevedi(text, source):
    #url = "https://website-translation1.p.rapidapi.com/translateLanguage/translate"
    #url = "https://google-translate1.p.rapidapi.com/language/translate/v2"
    url = "https://google-translate20.p.rapidapi.com/translate"

    target = "en"
    
    payload = "text="+text+"&tl="+target+"&sl=" + source
    headers = {
    'content-type': "application/x-www-form-urlencoded",
    'x-rapidapi-key': "8b34e40023msh8fcf4bc83335ee1p1b31f3jsn319bb3d8010f",
    'x-rapidapi-host': "google-translate20.p.rapidapi.com"
    }

    response = requests.request("POST", url, data=payload, headers=headers)

    d = json.loads(response.text)

    #print(d['data']['translations'][0]['translatedText'])

    #translations = d['data']['translations'][0]['translatedText']

    return str(d['data']['translation'])






