import cv2
print( cv2.__version__ )
import numpy as np
import glob
import random
import time

# Ucitavanje Yolo. Ubaciti tezine i konfiguracioni fajl u folder gde se nalazi i skripta
#net = cv2.dnn.readNet(r"D:\1 FAKULTET\Master\NM\steve-translator\yolo_custom_detection/yolov3_training_2000.weights", r"D:\1 FAKULTET\Master\NM\steve-translator\yolo_custom_detection/yolov3_testing.cfg")
net = cv2.dnn.readNet(r"yolov3_training_2000.weights", r"yolov3_testing.cfg")

# Name custom object
classes = ["text"]

# Images path
images_path = glob.glob(r"C:\Users\Dusan\Desktop\Yolo text proba\*.jpg")

layer_names = net.getLayerNames()
output_layers = [layer_names[i[0] - 1] for i in net.getUnconnectedOutLayers()]
colors = [[0, 0, 255]]
# Insert here the path of your images
random.shuffle(images_path)
# loop through all the images
for img_path in images_path:
    # Loading image
    img = cv2.imread(img_path)
    img = cv2.resize(img, None, fx=0.4, fy=0.4)
    height, width, channels = img.shape

    # pocetak merenja vremena
    start_time = time.time()
    # Detecting objects
    blob = cv2.dnn.blobFromImage(img, 0.00392, (416, 416), (0, 0, 0), True, crop=False)

    net.setInput(blob)
    outs = net.forward(output_layers)

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
                print(class_id)
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
    print(indexes)
    font = cv2.FONT_HERSHEY_PLAIN
    for i in range(len(boxes)):
        if i in indexes:
            x, y, w, h = boxes[i]
            # stavi proveru dominantne boje unutar pravougaonika plus minus 5 px oko centra
            label = str(classes[class_ids[i]])
            color = colors[class_ids[i]]
            cv2.rectangle(img, (x, y), (x + w, y + h), color, 2)
            cv2.putText(img, label, (x, y - 5), font, 1, color, 2)
    print("Vreme egzekucije: " + str(time.time() - start_time))

    cv2.imshow("Image", img)
    key = cv2.waitKey(0)

cv2.destroyAllWindows()