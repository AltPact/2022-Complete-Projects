import cv2
import numpy as np

class imageToMatrixClass:
    def __init__(self, images_paths, image_width, image_height):
        self.images_paths = images_paths
        self.image_width = image_width
        self.image_height = image_height
        self.images_size = (image_width * image_height)
    
    def get_matrix(self):
        col = len(self.images_paths)
        #create matrix with just 0s
        img_mat = np.zeros((self.images_size, col))

        i = 0
        # Loops through each point of matrix
        for path in self.images_paths:
            #grayscales the images and making it a matrix
            gray = cv2.imread(path, 0)
            gray_resized = cv2.resize(gray, (self.image_height, self.image_width))
            mat_gray = np.asmatrix(gray_resized)
            #making matrix into a vector
            #adding vector to whole image matrix
            img_mat[:,i] = mat_gray.ravel()
            i += 1
        return img_mat
