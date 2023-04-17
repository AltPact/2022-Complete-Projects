import cv2
from algoClass import mainAlgorithm
from imageMatrix import imageToMatrixClass
from dataset import datasetClass

reco_type = "image"

no_of_images_of_one_person = 8
dataset_obj = datasetClass(no_of_images_of_one_person)

imgPathTraining = dataset_obj.imgPathTraining
labelsTraining = dataset_obj.labelsTraining
NumImagesTraining = dataset_obj.NumImagesTraining

imgPathTesting = dataset_obj.imgPathTesting
labelsTesting = dataset_obj.labelsTesting
NumImagesTesting = dataset_obj.NumImagesTesting

imagesTarget = dataset_obj.imagesTarget

imgWidth, imgHeight = 50, 50
imageToMatrixClassObj = imageToMatrixClass(imgPathTraining, imgWidth, imgHeight)
imgMatrix = imageToMatrixClassObj.get_matrix()

#Algorithm class

mainAlgorithmObj = mainAlgorithm(imgMatrix, labelsTraining, imagesTarget, NumImagesTraining, imgWidth, imgHeight, qualityPercent=90)
new_coordinates = mainAlgorithmObj.reduce_dim()


#Recognition Process
if reco_type == "image":
    correctCounter = 0
    wrongCounter = 0
    i = 0
    for imgPath in imgPathTesting:
        img = mainAlgorithmObj.img_from_path(imgPath)
        mainAlgorithmObj.show_images("Recognize Image", img)
        new_cords_for_image = mainAlgorithmObj.new_cords(img)

        findedName = mainAlgorithmObj.recognize_face(new_cords_for_image)
        targetIndex = labelsTesting[i]
        originalName = imagesTarget[targetIndex]

        if findedName is originalName:
            correctCounter += 1
            print("Correct Result", " Name: ", findedName)
        else:
            wrongCounter += 1
            print("Wrong Result", " Name:", findedName)
        i += 1
    print("Total Correct", correctCounter)
    print("Total Wrong", wrongCounter)
    print("Percentage", correctCounter/(correctCounter + wrongCounter) * 100)




