import cv2
from PIL import Image
from flask import Flask, flash, request, redirect, url_for
import numpy as np


from yolo_text_detection import *
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

app = Flask(__name__)

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route("/")
def index():
    return "Hello World!"


@app.route('/upload', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        f = request.files['file']

        # pillow
        pil_image = Image.open(f).convert('RGB')
        open_cv_image = np.array(pil_image)
        # Convert RGB to BGR
        open_cv_image = open_cv_image[:, :, ::-1].copy()
        #cv2.imwrite('output.jpg', open_cv_image)

        find_text_areas(open_cv_image)

        return 'file uploaded successfully'

