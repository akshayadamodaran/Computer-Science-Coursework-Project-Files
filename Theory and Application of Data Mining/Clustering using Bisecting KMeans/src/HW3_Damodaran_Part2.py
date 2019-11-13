# -*- coding: utf-8 -*-
"""
Created on Mon Apr  1 15:06:54 2019

@author: aksha
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
import re
from sklearn.mixture import GaussianMixture
from sklearn.cluster import DBSCAN
from sklearn import metrics
from sklearn.preprocessing import StandardScaler
from scipy.spatial import distance_matrix
from itertools import combinations_with_replacement

datasets = ["dataset1.txt", "dataset2.txt"]

def get_records(dataset):
    with open(dataset) as f:
        data = f.readlines()
    
    data = [re.sub(r'[\t]', ' ',l[1:]).split() for l in data]

    records = []
    for doc in data:
        float_doc = list(map(float, doc))
        records.append(float_doc)
    
    records = np.array(records)
    return records

def find_correlation_matrix(records, labels):
        
    #creating the proximity matrix
    dist_mat = distance_matrix(records, records)
    
    indiSet = []
    for i in range(0,k):
        #indi is list of lists, each containing indices from 'records for each of the clusters
        indi = [j for j, x in enumerate(kmeans.labels_) if x == i]
        indiSet.append(indi)
    
    #creating incidence matrix
    for indSet in indiSet:
        for x,y in combinations_with_replacement(indSet, 2):
            incidence_mat[x][y] = 1
    corr = np.corrcoef(dist_mat,incidence_mat, rowvar=True)
    if(type(records) != 'pandas.core.frame.DataFrame'):
        df = pd.DataFrame(records)
    print("Plot of correlation: ")
    plt.matshow(corr)
    plt.xticks(range(len(df.columns)), df.columns)
    plt.yticks(range(len(df.columns)), df.columns)
    plt.colorbar()
    plt.show()
    return corr

    


for dataset in datasets:
    print("**Clustering {} using K Means**".format(dataset))

    records = get_records(dataset)
    X = pd.DataFrame.from_records(records)
        
    
    sse = {}
    for k in range(2, 6):
        kmeans = KMeans(n_clusters=k, random_state=0).fit(records)
        if((dataset=='dataset1.txt' and k==3) or (dataset=='dataset2.txt' and k==5)):
            incidence_mat = np.zeros((len(records),len(records)))
            kMeans_labels = kmeans.labels_
            corr_mat = find_correlation_matrix(records, kMeans_labels)
            print("Silhouette Score = {}".format(metrics.silhouette_score(X, kMeans_labels))) 
                
            
        sse[k] = kmeans.inertia_ # Inertia: Sum of distances of samples to their closest cluster center
        
    plt.figure()
    plt.plot(list(sse.keys()), list(sse.values()))
    plt.xlabel("Number of clusters")
    plt.ylabel("SSE")
    plt.show()
    
    
    print("**Clustering {} using EM**".format(dataset))
    gmm = GaussianMixture(n_components=5, covariance_type='full').fit(records)
    gmmPred = gmm.predict(records)
    corr_mat = find_correlation_matrix(records,gmmPred)
    print("Silhouette Score = {}".format(metrics.silhouette_score(records, gmmPred)))
    
    

df_one = pd.DataFrame.from_records(get_records(datasets[0]))
df_two = pd.DataFrame.from_records(get_records(datasets[1]))
scaler = StandardScaler()
df1_scaled = StandardScaler().fit_transform(df_one)

df2_scaled = StandardScaler().fit_transform(df_two)

print("**Clustering dataset1.txt using DBSCAN**")
db = DBSCAN(eps=0.3, min_samples=6).fit(df1_scaled)
labels1 = db.labels_
# Number of clusters in labels, ignoring noise if present.
n_clusters_ = len(set(labels1)) - (1 if -1 in labels1 else 0)
print('Estimated number of clusters: %d' % n_clusters_)
corr_mat1 = find_correlation_matrix(df_one,labels1)
print("Silhouette Score = {}".format(metrics.silhouette_score(df1_scaled, labels1)))
    
print("**Clustering dataset2.txt using DBSCAN**")
db = DBSCAN(eps=0.8, min_samples=6).fit(df2_scaled)
labels2 = db.labels_
# Number of clusters in labels, ignoring noise if present.
n_clusters_ = len(set(labels2)) - (1 if -1 in labels2 else 0)
print('Estimated number of clusters: %d' % n_clusters_)
corr_mat2 = find_correlation_matrix(df_two,labels2)
print("Silhouette Score = {}".format(metrics.silhouette_score(df2_scaled, labels2)))
    