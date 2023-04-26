import os

class datasetClass:
    def __init__(self, required_no):
        #Dataset Path
        self.dir = ("images/ORL")

        self.imgPathTraining = []
        self.labelsTraining = []
        self.NumImagesTraining = []

        self.imgPathTesting = []
        self.labelsTesting = []
        self.NumImagesTesting = []

        self.imagesTargetArray = []
        self.imagesTargetSet = {}

        per_no = 0
        #Looping through all the traing data that is within the "images/" folder
        for name in os.listdir(self.dir):
            dir_path = os.path.join(self.dir, name)
            if os.path.isdir(dir_path):
                # print("Length: ",len(os.listdir(dir_path)))
                #Checks the folder has at least 8 images in it
                if len(os.listdir(dir_path)) >= required_no:
                    i = 0
                    for img_name in os.listdir(dir_path):
                        img_path = os.path.join(dir_path,img_name)

                        if i < required_no:
                            self.imgPathTraining += [img_path]
                            self.labelsTraining += [per_no]
                            # Check to see if this person img is already in the self so no duplicants
                            if len(self.NumImagesTraining) > per_no:
                                # add 1 more to training
                                self.NumImagesTraining[per_no] += 1
                            else:
                                self.NumImagesTraining = [1]
                            
                            if i is 0:
                                self.imagesTargetArray += [name]
                                self.imagesTargetSet[per_no] = name
                            
                            else:
                                self.imgPathTesting += [img_path]
                                self.labelsTesting += [per_no]

                                if len(self.NumImagesTesting) > per_no:
                                    self.NumImagesTesting[per_no] += 1
                                else:
                                    self.NumImagesTesting += [1]
                            i += 1
                        per_no += 1
        print("Per_no :", per_no)
        print("i: ", i)
        print("Label Testing array: ", self.labelsTesting)
        print("Image Target array: ", self.imagesTargetArray)