import numpy as np
import cv2
import scipy.linalg as s_linalg

class mainAlgorithm:
    def __init__(self, imgMatrix, imgLabels, imgTargets, numOfElements, imgWidth, imgHeight, qualityPercent):
        self.imgMatrix = imgMatrix
        self.imgLabels = imgLabels
        self.imgTargets = imgTargets
        self.numOfElements = numOfElements
        self.imgWidth = imgWidth
        self.imgHeight = imgHeight
        self.qualityPercent = qualityPercent

        #subtract mean face
        mean = np.mean(self.imgMatrix, 1)
        self.mean_face = np.asmatrix(mean).T
        self.imgMatrix -= self.mean_face
    
    #Find how many vectors we need 
    def give_P_value(self, eig_vals):
        sumOrginals = np.sum(eig_vals)
        sumThreshold = sumOrginals * self.qualityPercent/100
        sumTemp = 0
        P = 0
        while sumTemp < sumThreshold:
            sumTemp += eig_vals[P]
            P += 1
        return P
    
    #reduces dimensions of the eignvalues 
    def reduce_dim(self):
        u, eig_vals, v_t = s_linalg.svd(self.imgMatrix, full_matrices=True)
        P = self.give_P_value(eig_vals)
        self.new_bases = u[:, 0:P]
        self.new_coordinates = np.dot(self.new_bases)

    #Finds new coordinates of single image
    def new_cords(self, single_image):
        img_vec = np.asmatrix(single_image).ravel()
        img_vec = img_vec.T
        #Making new mean face based on the new image
        new_mean =((self.mean_face * len(self.imgLabels)) + img_vec)/(len(self.imgLabels) + 1)
        #Minus the new mean from the image vector
        img_vec = img_vec - new_mean
        return np.dot(self.new_bases.T, img_vec)
    
    #Creating Temporary matrix just for that class
    def recognize_face(self, newCordinatesOfImage):
        classes = len(self.numOfElements)
        start = 0
        dist = []
        for i in range(classes):
            temp_imgs = self.new_coordinates[:, int(start):int(start + self.numOfElements[i])]
            mean_temp = np.asmatrix(np.mean(temp_imgs, 1)).T
            star = start + self.numOfElements[i]
            dist_temp = np.linalg.norm(newCordinatesOfImage - mean_temp)
            #Create distance Vector
            dist += [dist_temp]
        
        #find class is a minimum distance from the vector (which one looks the closest)
        min_pos = np.argmin(dist)
        # target names
        return self.imgTargets[min_pos]
    
    def img_from_path(self,path):
        gray = cv2.imread(path, 0)
        return cv2.resize(gray, (self.imgWidth, self.imgHeight))
    
    def new_to_old_cords(self, newCords):
        return self.mean_face + (np.asmatrix(np.dot(self.new_bases, newCords))).T
    
    def show_images(self, labelName, oldCords):
        oldCordsMatrix = np.reshape(oldCords, [self.imgWidth, self.imgHeight])
        # convert into integers between 0 - 255
        oldCordsInt = np.array(oldCordsMatrix, dtype=np.uint8)
        #Make it size of 500 so it can be visiable
        resizedImage = np.resize(oldCordsInt, (500,500))
        cv2.imshow(labelName, resizedImage)
        cv2.waitKey()

    #change pixel decisity so it's not really dark
    def show_eigne_faces(self, min_pix_int, max_pix_int, eig_face_no):
        ev = self.new_bases[:, eig_face_no: eig_face_no + 1]
        min_orig = np.min(ev)
        max_orig = np.max(ev)

        ev = min_pix_int + (((max_pix_int - min_pix_int)/(max_orig - min_orig)) * ev)
        self.show_images("Eigen Face "+str(eig_face_no), ev)

    
