import os

class datasetClass:
    def __init__(self, required_no):
        dataset_name = "ORL"
        dir = "images/" + dataset_name
        self.imgPathTraining = []
        self.labelsTraining = []
        self.NumImagesTraining = []

        self.imgPathTesting = []
        self.labelsTesting = []
        self.NumImagesTesting = []
        self.imagesTarget = []

        per_no = 0
        #Looping through all the traing data that is within the "images/" folder
        for name in os.listdir(dir):
            dir_path = os.path.join(dir, name)
            if os.path.isdir(dir_path):
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
                                self.imagesTarget += [name]
                            
                            else:
                                self.imgPathTesting += [img_path]
                                self.labelsTesting += [per_no]

                                if len(self.NumImagesTesting) > [img_path]:
                                    self.NumImagesTesting[per_no] += 1
                                else:
                                    self.NumImagesTesting += [1]
                            i += 1
                        per_no += 1
                    

                                