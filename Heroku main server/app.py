from PIL import Image
from flask import Flask, request, send_file
from yolo_text_detection import *

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

app = Flask(__name__)

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/justfindtext', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        f = request.files['file']

        # pillow w
        pil_image = Image.open(f).convert('RGB')
        open_cv_image = np.array(pil_image)
        del pil_image
        # Convert RGB to BGR
        open_cv_image = open_cv_image[:, :, ::-1].copy()
        #cv2.imwrite('output.jpg', open_cv_image)

        #ret_img = find_text_areas(open_cv_image)

        #im_pil = Image.fromarray(ret_img)
        ret_string = find_text_areas(open_cv_image, 'just-find-text')

        #im_pil = Image.fromarray(ret_img)
        #ret_strint = ''.join(filter(str.isalpha, ret_strint))
        return ret_string

    else:

        return '<h1>Dobro dovdojte Samo vracanje pronadjenog teksta</h1>'


@app.route('/it', methods=['GET', 'POST'])
def upload_file_it():
    if request.method == 'POST':
        f = request.files['file']

        # pillow w
        pil_image = Image.open(f).convert('RGB')
        open_cv_image = np.array(pil_image)
        del pil_image
        # Convert RGB to BGR
        open_cv_image = open_cv_image[:, :, ::-1].copy()
        #cv2.imwrite('output.jpg', open_cv_image)

        #ret_img = find_text_areas(open_cv_image)

        #im_pil = Image.fromarray(ret_img)
        ret_string = find_text_areas(open_cv_image, 'it')

        #im_pil = Image.fromarray(ret_img)
        #ret_strint = ''.join(filter(str.isalpha, ret_strint))
        return ret_string

    else:

        return '<h1>Dobro dovdojte Italijani</h1>'

@app.route('/de', methods=['GET', 'POST'])
def upload_file_de():
    if request.method == 'POST':
        f = request.files['file']

        # pillow w
        pil_image = Image.open(f).convert('RGB')
        open_cv_image = np.array(pil_image)
        del pil_image
        # Convert RGB to BGR
        open_cv_image = open_cv_image[:, :, ::-1].copy()
        #cv2.imwrite('output.jpg', open_cv_image)

        #ret_img = find_text_areas(open_cv_image)

        #im_pil = Image.fromarray(ret_img)
        ret_string = find_text_areas(open_cv_image, 'de')

        #im_pil = Image.fromarray(ret_img)
        #ret_strint = ''.join(filter(str.isalpha, ret_strint))
        return ret_string

    else:

        return '<h1>Dobro dovdojte nemci</h1>'

@app.route('/ru', methods=['GET', 'POST'])
def upload_file_ru():
    if request.method == 'POST':
        f = request.files['file']

        # pillow w
        pil_image = Image.open(f).convert('RGB')
        open_cv_image = np.array(pil_image)
        del pil_image
        # Convert RGB to BGR
        open_cv_image = open_cv_image[:, :, ::-1].copy()
        #cv2.imwrite('output.jpg', open_cv_image)

        #ret_img = find_text_areas(open_cv_image)

        #im_pil = Image.fromarray(ret_img)
        ret_string = find_text_areas(open_cv_image, 'ru')

        #im_pil = Image.fromarray(ret_img)
        #ret_strint = ''.join(filter(str.isalpha, ret_strint))
        return ret_string

    else:

        return '<h1>Dobro dovdojte Rusi</h1>'


