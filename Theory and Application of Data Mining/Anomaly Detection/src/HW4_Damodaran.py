# -*- coding: utf-8 -*-
"""
Created on Tue Apr 16 22:43:08 2019

@author: aksha
"""
import os
import fnmatch
import pandas as pd
import re
import numpy as np
from sklearn.decomposition import PCA
from sklearn.model_selection import train_test_split
from scipy.spatial.distance import pdist, squareform
import scikitplot as skplt
import matplotlib.pyplot as plt
from sklearn.preprocessing import label_binarize
from sklearn.metrics import roc_curve, auc
#


def getint(name):
    fname = name.split('.')
    num = fname[0]
    intpart = [int(s) for s in num if s.isdigit()]
    print(intpart)
    return intpart[0]

def get_records(dataset):
    with open(dataset) as f:
        data = f.readlines()
    
    data = [re.sub(r'[\t]', ' ',l).split() for l in data]
    float_data = []
    for doc in data:
        doc = list(map(float, doc))
        float_data.extend(doc)
    return float_data

def find_kdist(distance_df,k):
    observations = distance_df.columns
    k_dist_list = []
    for observation in observations:
        dist = distance_df[observation].nsmallest(k+1).iloc[k]
        k_dist_list.append(dist)
    return k_dist_list

def find_minPtsreachdist(k_dist_list, distance_df, knn_for_all_points):
    minPts_reach_dist_list = []
    observations = distance_df.columns
    for observation in observations:
        minPts_reach_dist_obs = []
        for i in range(len(knn_for_all_points[observation])):
            reach_dist = max(k_dist_list[observation], (distance_df.iloc[observation][knn_for_all_points[observation][i]]))
            minPts_reach_dist_obs.append(reach_dist)
        minPts_reach_dist_list.append(minPts_reach_dist_obs)
    return minPts_reach_dist_list

def find_knn_forall_points(points,minPts,distance_df):
    knn_for_all_points = []
    for i in points:
        knn_for_i_mat = distance_df.nsmallest(minPts,i)
        knn_indices_for_i = list(knn_for_i_mat.index)
        knn_indices_for_i = knn_indices_for_i[1:]
        knn_for_all_points.append(knn_indices_for_i)
    return knn_for_all_points
    

path = 'Base'
testPath = 'TestWT'
label = []

baselineFiles = [os.path.join(subdir,f)
for subdir, dirs, files in sorted(os.walk(path))
for f in fnmatch.filter(files, '*.txt')]

baseline = []
directory =os.path.join(path)
for subdir,dirs ,files in os.walk(directory):
    files.sort(key=lambda f: int(''.join(filter(str.isdigit, f))))
    for file in files:
        if fnmatch.filter(files, '*.txt'):
            f=open(os.path.join(subdir, file),'r')
            a = get_records(os.path.join(subdir, file))
            baseline.append(a)
fulldata = pd.DataFrame(baseline)


for f in baselineFiles:
    label.append(1 if f[9]=='M' else 0)
    labels = pd.DataFrame(label, columns = ['Labels'])
    
baselinedata, mal_line = fulldata[:400], fulldata[400:]


baselinedata = baselinedata.reset_index(drop=True)
mal_line = mal_line.reset_index(drop=True)



pca1 = PCA(n_components=15)
pca_m1 = pca1.fit(mal_line)
reduced_features_mal = pca1.transform(mal_line)
reduced_features_mal = pd.DataFrame(reduced_features_mal)

pca2 = PCA(n_components=15)
pca_m2 = pca2.fit(baselinedata)
reduced_features_base = pca2.transform(baselinedata)
reduced_features_base = pd.DataFrame(reduced_features_base)

train_df, test_df, train_lbl, test_lbl = train_test_split( reduced_features_base, labels[:400], test_size=0.3, random_state=42)

combined_test_df = pd.concat([test_df, reduced_features_mal])
combined_labels_df = pd.concat([test_lbl,labels[400:]])

distances = pdist(combined_test_df.values, metric='minkowski')
distances_bl = pdist(reduced_features_base.values, metric='minkowski')
dist_matrix = squareform(distances)
dist_matrix_bl = squareform(distances_bl)
distance_df = pd.DataFrame(dist_matrix)
distance_bl_df = pd.DataFrame(dist_matrix_bl)

k=7
k_dist_list = find_kdist(distance_df,k)
k_dist_list_bl = find_kdist(distance_bl_df,k)


points = distance_df.columns
points_bl = distance_bl_df.columns
minPts = 16
knn_for_all_points = find_knn_forall_points(points,minPts, distance_df)
knn_for_all_points_bl = find_knn_forall_points(points_bl,minPts, distance_bl_df)

rd_for_minPts = find_minPtsreachdist(k_dist_list,distance_df,knn_for_all_points)
rd_for_minPts_bl = find_minPtsreachdist(k_dist_list_bl,distance_bl_df,knn_for_all_points_bl)

lrd_qs = []
for r_dts_q in rd_for_minPts:
    lrd_qs.append((minPts-1)/sum(r_dts_q))
    
lrd_qs_bl = []
for r_dts_q in rd_for_minPts_bl:
    lrd_qs_bl.append((minPts-1)/sum(r_dts_q))

lof_qs = []
for q in points:
    lrd_neigh_sum = 0
    for neigh in knn_for_all_points[q]:
        lrd_neigh_sum += lrd_qs[neigh]
    lof_q = lrd_neigh_sum / (lrd_qs[q] * minPts)
    lof_qs.append(lof_q)
    
    
lof_qs_bl = []
for q in points_bl:
    lrd_neigh_sum = 0
    for neigh in knn_for_all_points_bl[q]:
        lrd_neigh_sum += lrd_qs_bl[neigh]
    lof_q = lrd_neigh_sum / (lrd_qs_bl[q] * minPts)
    lof_qs_bl.append(lof_q)
    
testFiles = [os.path.join(subdir,f)
for subdir, dirs, files in sorted(os.walk(testPath))
for f in fnmatch.filter(files, '*.txt')]

testline = []
tstdirectory =os.path.join(testPath)
for subdir,dirs ,files in os.walk(tstdirectory):
    files.sort(key=lambda f: int(''.join(filter(str.isdigit, f))))
    for file in files:
        if fnmatch.filter(files, '*.txt'):
            f=open(os.path.join(subdir, file),'r')
            a = get_records(os.path.join(subdir, file))
            testline.append(a)
testdata = pd.DataFrame(testline)

pca3 = PCA(n_components=15)
pca_m3 = pca3.fit(baselinedata)
reduced_features_test = pca3.transform(testdata)
reduced_features_test = pd.DataFrame(reduced_features_test)

combined_test_base = pd.concat([reduced_features_test, reduced_features_base])

distances_tst = pdist(combined_test_base.values, metric='minkowski')
dist_matrix_tst = squareform(distances_tst)
distance_tst_df = pd.DataFrame(dist_matrix_tst)   
k_dist_list_tst = find_kdist(distance_tst_df,k)

points_tst = distance_tst_df.columns
knn_for_all_points_tst = find_knn_forall_points(points_tst,minPts, distance_tst_df)
rd_for_minPts_tst = find_minPtsreachdist(k_dist_list_tst,distance_tst_df,knn_for_all_points_tst)

lrd_qs_tst = []
for r_dts_q in rd_for_minPts_tst:
    lrd_qs_tst.append((minPts-1)/sum(r_dts_q))
    
lof_qs_tst = []
for q in points_tst:
    lrd_neigh_sum = 0
    for neigh in knn_for_all_points_tst[q]:
        lrd_neigh_sum += lrd_qs_tst[neigh]
    lof_q = lrd_neigh_sum / (lrd_qs_tst[q] * minPts)
    lof_qs_tst.append(lof_q)
    
lof_qs_bl.sort()
my_p_list = []
N = len(lof_qs_bl)
for lof_q in lof_qs:
    b = sum(lof_q_bl >= lof_q for lof_q_bl in lof_qs_bl)
    my_p_list.append((b+1)/(N+1))
    
conf = 0.9
my_t_labels = []
for indx,p in enumerate(my_p_list):
    if(p<0.1):
        my_t_labels.append(1)
    else:
        my_t_labels.append(0)
        
my_p_list_arr = np.asarray(my_p_list)
true_labels = [0]*len(test_df)
true_labels.extend([1]*len(reduced_features_mal))

#Plot ROC
#skplt.metrics.plot_roc(true_labels, np.reshape(my_p_list_arr,(len(my_p_list_arr),1)))
#plt.show()

p_list = []
N = len(lof_qs_bl)
for lof_q_tst in lof_qs_tst[:499]:
    b = sum(lof_q_bl >= lof_q_tst for lof_q_bl in lof_qs_bl)
    p_list.append((b+1)/(N+1))
    
with open('HW4Output', 'w') as f:
    for item in p_list:
        f.write("%s\n" % item) 