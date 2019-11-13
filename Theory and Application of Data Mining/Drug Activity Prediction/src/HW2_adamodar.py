# -*- coding: utf-8 -*-
"""
Created on Wed Feb 27 19:55:28 2019

@author: aksha
"""

import re
import numpy as np
from sklearn.neural_network import MLPClassifier

from sklearn.discriminant_analysis import LinearDiscriminantAnalysis
from sklearn.decomposition import PCA
from sklearn.decomposition import SparsePCA
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import accuracy_score
from sklearn.model_selection import cross_val_predict
from sklearn import metrics
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.ensemble import AdaBoostClassifier
from sklearn.svm import SVC,NuSVC
from sklearn.model_selection import cross_val_score
from sklearn.linear_model import LogisticRegression
from imblearn.over_sampling import SMOTE
from sklearn.decomposition import TruncatedSVD
from sklearn.random_projection import sparse_random_matrix
from sklearn.linear_model import Perceptron
from sklearn import svm
import pandas as pd

train_file = "traindrugs.dat"
test_file = "testdrugs.dat"

feature_size = 100001
k_folds = 10




def load(filename, ftype):
    with open(filename, "r") as rfile:
        lines = rfile.readlines()

    if ftype == "train":
        labels = [int(l[0]) for l in lines]
        for index, item in enumerate(labels):
            if (item == 0):
                labels[index] = -1
        docs = [re.sub(r'[^\w]', ' ',l[1:]).split() for l in lines]

    else:
        labels = []
        docs = [re.sub(r'[^\w]', ' ',l).split() for l in lines]

    
    feat_df = pd.DataFrame(columns=['drugs-data'])

    
    for index,doc in enumerate(docs):
        doc = " ".join(doc)
        feat_df.loc[index] = doc

    
    return feat_df, labels


print('Starting processing for drug activity prediction')

print("Loading training data")
# Loading train.dat file
features, labels = load(train_file, "train")


#Using Dimensionality Reduction on train data
print("Applying CountVectorizer and Reducing Dimensions using Truncated SVD on train data")


vectorizer = CountVectorizer() 
vectorized_features = vectorizer.fit_transform(features['drugs-data'])
svd_trunc = TruncatedSVD(algorithm='randomized', n_components=1000, n_iter=40, random_state=42)
svd_trunc_m = svd_trunc.fit(vectorized_features, labels)
reduced_features = svd_trunc_m.transform(vectorized_features)


#Using oversampling SMOTE

print("Oversampling data using SMOTE!")
sm = SMOTE(random_state=42,kind='svm')
reduced_features, labels = sm.fit_sample(reduced_features, labels)


#processing test data
print("Loading test data")

df_test = pd.read_csv('testdrugs.dat', header=None, names=['data'])
#test_features, test_labels = load(test_file, "test")


print("Reducing Dimensions using Truncated SVD on test data")
#test_reduced_features = pca_m.transform(test_features)
test_vectorized_features = vectorizer.transform(df_test['data'])
test_reduced_features = svd_trunc_m.transform(test_vectorized_features)



# Classifying
names = ["Decision Tree"]
classifiers = [DecisionTreeClassifier(random_state=53,class_weight={-1: 1, 1: 1.5})]


print('Starting classification!!')

for name, clf in zip(names, classifiers):
    print('Report on ' + name)
    cv_predicted = cross_val_predict(clf, reduced_features, labels, cv=k_folds)

    print(metrics.classification_report(labels, cv_predicted))

    scores = cross_val_score(clf, reduced_features, labels)

    print('\nCross validation scores: ')
    print(scores.mean())

    #training classifier
    clf.fit(reduced_features, labels)

    # Predict test labels
    test_predicted = clf.predict(test_reduced_features)

    print('Test predicted for ' + name)

    result_file = 'format.csv'

    print('Output stored in', result_file)

    output = open(result_file, 'w')
    for t in test_predicted:
        if int(t) == -1:
            t = 0
        output.write(str(t))
        output.write("\n")
    output.close()

print('Finished!')