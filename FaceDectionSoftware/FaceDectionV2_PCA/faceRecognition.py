import cv2
import time
import numpy as np

from algoClass import mainAlgorithm
from PCA import pca_class
from TwoDPCA import twoDPcaClass
from TwoD_Square_PCA import twoDSquarePcaClass

from imageMatrix import imageToMatrixClass
from images_matrix_for_2d_square_pca import imagesToMatrixClassForTwoD
from dataset import datasetClass

# Algorithm types (pca, 2d-pca, 2d2-pca)
algorithmType = "pca"

#single image = 0
#video = 1
#group image = 2
reco_type = "video"

#Number of images for Training, the other images in the folder will be used for testing
no_of_images_of_one_person = 8
dataset_obj = datasetClass(no_of_images_of_one_person)

#Data for training
imgPathTraining = dataset_obj.imgPathTraining
labelsTraining = dataset_obj.labelsTraining
NumImagesTraining = dataset_obj.NumImagesTraining
# imagesTarget = dataset_obj.imagesTargetArray
imagesTarget = dataset_obj.imagesTargetArray

#Data for Testing
imgPathTesting = dataset_obj.imgPathTesting
labelsTesting = dataset_obj.labelsTesting
NumImagesTesting = dataset_obj.NumImagesTesting


imgWidth, imgHeight = 50, 50
imageToMatrixClassObj = imageToMatrixClass(imgPathTraining, imgWidth, imgHeight)
imgMatrix = imageToMatrixClassObj.get_matrix()

trainingStartTime = time.process_time()

if algorithmType == "pca":
    i_t_m_c = imageToMatrixClass(imgPathTraining, imgWidth, imgHeight)
    scaledFace = i_t_m_c.get_matrix()
    cv2.imshow("Original Image", cv2.resize(np.array(np.reshape(scaledFace[:,1][imgHeight, imgWidth]), dtype = np.uint8),(200,200)))
    cv2.waitKey()
else:
    i_t_m_c = imagesToMatrixClassForTwoD(imgPathTraining, imgWidth, imgHeight)
    scaledFace = i_t_m_c.get_matrix()
    cv2.imshow("Original Image", cv2.resize(scaledFace[0]),(200,200))
    cv2.waitKey()

if algorithmType == "pca":
    currentAlgorithm = pca_class(scaledFace, labelsTesting, imagesTarget, NumImagesTraining, 90)
elif algorithmType == "2d-pca":
    currentAlgorithm = twoDPcaClass(scaledFace, labelsTesting, imagesTarget)
else:
    currentAlgorithm = twoDSquarePcaClass(scaledFace, labelsTesting, imagesTarget)

#Algorithm class

mainAlgorithmObj = mainAlgorithm(imgMatrix, labelsTraining, imagesTarget, NumImagesTraining, imgWidth, imgHeight, qualityPercent=90)
new_coordinates = mainAlgorithmObj.reduce_dim()
# mainAlgorithmObj.show_eigen_faces(imgWidth, imgHeight, 50, 150, 0)

trainingTime = time.process_time() - trainingStartTime

#Recognition Process
if reco_type == 0:
    time_start = time.process_time()

    # Keeping track of time and success rate of algorithms
    correctCounter = 0
    wrongCounter = 0
    i = 0
    netTimeOfReco = 0

    for imgPath in imagesNamesForTesting:
        timeStart = time.process_time()
        findedName = mainAlgorithm.recognize_face(mainAlgorithm.new_cords(imgPath, imgHeight, imgWidth))
        timeElapsed = (time.process_time() - timeStart)
        netTimeOfReco += timeElapsed
        
        if findedName is recName:
            correctCounter += 1
            print("Correct ", " Name: ", findedName)
        else:
            wrongCounter += 1
            print("Wrong: ", "Real Name: ", recName, "Rec Y: ", rec_y, "Found Name: ", findedName)
        i+= 1
        print("i = ", i)
    
    print("Correct Counter: ", correctCounter)
    print("Wrong Counter: ", wrongCounter)
    print("Total Test Images", i)
    print("Percent of correct Identification: ", correctCounter/i*100)
    print("Total Person: ", len(imagesTarget))
    print("Total Train Images: ", no_of_images_of_one_person * len(target_names))
    print("Total Time Taken for reco:", timeElapsed)
    print("Time Taken for one reco:", timeElapsed/i)
    print("Training Time: ", trainingTime)

# if reco_type == "image":
#     correctCounter = 0
#     wrongCounter = 0
#     i = 0

#     for imgPath in imgPathTesting:
#         print("Testing - Image Path: ", imgPath)
#         img = mainAlgorithmObj.img_from_path(imgPath)
#         mainAlgorithmObj.show_images("Recognize Image", img)
#         # new_cords_for_image = mainAlgorithmObj.new_cords(imgPath, imgWidth, imgHeight)

#         findedName = mainAlgorithmObj.recognize_face(mainAlgorithmObj.new_cords(imgPath, imgWidth, imgHeight))
#         targetIndex = labelsTesting[i]
#         print("Target Index: ", targetIndex)
#         # originalName = imagesTargetArray[targetIndex]
#         originalName = imagesTargetArray[i]
#         if findedName is originalName:
#             correctCounter += 1
#             print("Correct Result", " Name: ", findedName, "Original Name: ", originalName)
#         else:
#             wrongCounter += 1
#             # print("Wrong Result", "Found Name: ", findedName, "Original Name: ", originalName)
#         i += 1
#         print("i = ", i)
    
#     print("Total Correct", correctCounter)
#     print("Total Wrong", wrongCounter)
#     print("Percentage", correctCounter/(correctCounter + wrongCounter) * 100)

#For Group photos
if reco_type == "Group":
    face_cascade = cv2.CascadeClassifier('cascades/data/harrcascade_frontal_alt2.xml')
    dir = "images/GroupImages/"    

    #Make Black and white
    img = cv2.imread(dir + "group.jpg", 0)

    faces = face_cascade.detectMultiScale(img, scaleFactor=1.5, minNeighbors=3)

    for (x,y,w,h) in faces:
        # Put the Squares around where the detected face is
        roi = img[y: y+h, x:x+h]
        scaled = cv2.resize(roi, (imgWidth, imgHeight))
        rec_color = (0, 255, 0)
        rec_stroke = 3
        cv2.rectangle(img, (x,y), (x+w, y+h), rec_color, rec_stroke)

        #Recognizes face
        new_cord = mainAlgorithmObj.new_cords(scaled)
        name = mainAlgorithmObj.recognize_face(new_cord)
        font = cv2.FONT_HERSHEY_COMPLEX
        font_color = (255, 0, 0)
        font_stroke = 3
        cv2.putText(img, name, (x,y), font, 5, font_color, font_stroke, cv2.LINE_AA)
    
    frame = cv2.resize(img, (1080, 568))
    cv2.imshow("Frame", frame)
    cv2.waitKey()

#For videos
if reco_type == "video":
    # face_cascade = cv2.CascadeClassifier('cascades/data/harrcascade_frontal_alt2.xml')
    # face_cascade = cv2.CascadeClassifier("C:\\Users\\ademp\\OneDrive\\Documents\\2022 Complete Projects\\2022-Complete-Projects\\FaceDectionSoftware\\FaceDectionV2_PCA\\cascades\\data\\harrcascade_frontal_alt2.xml")
    face_cascade = cv2.CascadeClassifier('cascades/data/haarcascade_frontalface_alt2.xml')
    # face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'harrcascade_frontal_alt2.xml')
    # dir = "images/GroupImages/"    

    # #Make Black and white
    # img = cv2.imread(dir + "group.jpg", 0)

    cap = cv2.VideoCapture(0)
    while True:
        ret, img = cap.read()
        img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)


        faces = face_cascade.detectMultiScale(img, scaleFactor=1.5, minNeighbors=3)

        for (x,y,w,h) in faces:
            # Put the Squares around where the detected face is
            roi = img[y: y+h, x:x+h]
            scaled = cv2.resize(roi, (imgWidth, imgHeight))
            rec_color = (0, 255, 0)
            rec_stroke = 3
            cv2.rectangle(img, (x,y), (x+w, y+h), rec_color, rec_stroke)

            #Recognizes face
            new_cord = mainAlgorithmObj.new_cords(scaled)
            name = mainAlgorithmObj.recognize_face(new_cord)
            font = cv2.FONT_HERSHEY_COMPLEX
            font_color = (255, 0, 0)
            font_stroke = 3
            cv2.putText(img, name, (x,y), font, 5, font_color, font_stroke, cv2.LINE_AA)
        
        frame = cv2.resize(img, (1080, 568))
        cv2.imshow("Frame", frame)
        if cv2.waitKey(20) & 0xFF == ord('q'):
            break
    cap.release()
    cv2.destroyAllWindows()