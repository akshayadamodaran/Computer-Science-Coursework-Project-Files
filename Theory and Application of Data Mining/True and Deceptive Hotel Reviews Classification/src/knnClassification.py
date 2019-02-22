# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""
import os
import fnmatch
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split
from nltk.corpus import stopwords
import re

from sklearn.metrics import accuracy_score
from sklearn.metrics.pairwise import cosine_distances
from textblob import Word
from collections import Counter




def train(X_train, y_train):
    # do nothing 
    # The KNN algorithm is a Lazy Learner. It chooses to memorize the training 
    # instances which are subsequently used as “knowledge” for the prediction phase. 
    # Therefore in train function, we do nothing.
    return

def kNearestNeighbor(X_train, y_train, x_test, predictions,k):
    train(X_train, y_train)
    
    distance = cosine_distances(x_test, X_train)
    
    for i in range(distance.shape[0]):
        train_dist = distance[i]
        df = []
        for d in range(len(train_dist)):
            df.append([train_dist[d], y_train.iloc[d]['Labels']])
        df.sort(key=lambda x:x[0])
  
        k_truth_values = []
        # make a list of the k neighbors' targets
        for i in range(k):
            k_truth_values.append(df[i][1])   
        # return most common target
        predictions.append(Counter(k_truth_values).most_common(1))
    return predictions

# getint function is used to return the integer part of the test files names for sorting them in the order that they appear the the test data directory 
def getint(name):
    fname = name.split('.')
    num = fname[0]
    return int(num)



path = '../Training'
testPath = '../CS584testHW1'
label = []

configfiles = [os.path.join(subdir,f)
for subdir, dirs, files in os.walk(path)
    for f in fnmatch.filter(files, '*.txt')]


# A dataframe of ‘labels’ is created for each of the text files in each sub-directory of the training dataset(Deceptive or Truthful).


for f in configfiles:
    c = re.search('(trut|deceptiv)\w',f)
    label.append(c.group())
    
    labels = pd.DataFrame(label, columns = ['Labels'])
    


# Training data put in to a panda dataframe
review = []
directory =os.path.join("../Training")
for subdir,dirs ,files in os.walk(directory):
    for file in files:
        if fnmatch.filter(files, '*.txt'):
            f=open(os.path.join(subdir, file),'r')
            a = f.read()
            review.append(a)
            
reviews = pd.DataFrame(review, columns = ['HotelReviews'])

# Dataframes of the labels and training reviews are merged and pre-processing is then done on the text reviews in the dataframe.
result = pd.merge(reviews, labels,right_index=True,left_index = True)

# Text pre-processing for the training data set
result['HotelReviews'] = result['HotelReviews'].apply(lambda x: " ".join(x.lower() for x in x.split()))
result['HotelReviews'] = result['HotelReviews'].str.replace('[^\w\s]','')
stop = stopwords.words('english')
result['HotelReviews'] = result['HotelReviews'].apply(lambda x: " ".join(x for x in x.split() if x not in stop))

freq = pd.Series(' '.join(result['HotelReviews']).split()).value_counts()[:10]
freq = list(freq.index)
result['HotelReviews'] = result['HotelReviews'].apply(lambda x: " ".join(x for x in x.split() if x not in freq))

freq = pd.Series(' '.join(result['HotelReviews']).split()).value_counts()[-10:]
freq = list(freq.index)
result['HotelReviews'] = result['HotelReviews'].apply(lambda x: " ".join(x for x in x.split() if x not in freq))
result['HotelReviews'] = result['HotelReviews'].apply(lambda x: " ".join([Word(word).lemmatize() for word in x.split()]))





testreview = []
directory =os.path.join(testPath)
for subdir,dirs ,files in os.walk(directory):
    files = sorted(files, key=getint)
    for file in files:
        if fnmatch.filter(files, '*.txt'):
            f=open(os.path.join(subdir, file),'r')
            a = f.read()
            testreview.append(a)
testreviews = pd.DataFrame(testreview, columns = ['testHotelReviews'])

#Text pre-processing for the test data set
testreviews['testHotelReviews'] = testreviews['testHotelReviews'].apply(lambda x: " ".join(x.lower() for x in x.split()))
testreviews['testHotelReviews'] = testreviews['testHotelReviews'].str.replace('[^\w\s]','')
stop = stopwords.words('english')
testreviews['testHotelReviews'] = testreviews['testHotelReviews'].apply(lambda x: " ".join(x for x in x.split() if x not in stop))

freq = pd.Series(' '.join(testreviews['testHotelReviews']).split()).value_counts()[:10]
freq = list(freq.index)
testreviews['testHotelReviews'] = testreviews['testHotelReviews'].apply(lambda x: " ".join(x for x in x.split() if x not in freq))
freq = pd.Series(' '.join(testreviews['testHotelReviews']).split()).value_counts()[-10:]
freq = list(freq.index)
testreviews['testHotelReviews'] = testreviews['testHotelReviews'].apply(lambda x: " ".join(x for x in x.split() if x not in freq))
testreviews['testHotelReviews'] = testreviews['testHotelReviews'].apply(lambda x: " ".join([Word(word).lemmatize() for word in x.split()]))



# Using the TfIdfVectorizer to vectorize the training and test data
tfidfVectorizer = TfidfVectorizer(analyzer='word', token_pattern=r'\w{1,}', max_features=5000, ngram_range=(1,1))
tfidfVectorizer.fit(result['HotelReviews'])
X_train_tf = tfidfVectorizer.transform(result['HotelReviews'])
X_test_tf = tfidfVectorizer.transform(testreviews['testHotelReviews'])


# splitting the training data for k-fold validation
X_train, X_test, y_train, y_test = train_test_split(X_train_tf, labels, test_size=0.33, random_state=42)

predictions = []
kNearestNeighbor(X_train, y_train, X_test, predictions, 7)
predList = []
labelList = []
for p in range(len(predictions)):
    predList.append(predictions[p][0][0])
    labelList.append(y_test['Labels'].iloc[p])
# evaluating accuracy
accuracy = accuracy_score(labelList, predList) * 100
print('\nThe accuracy of OUR classifier is %d%%' % accuracy)


# Calling kNearestNeighbor function for the actual test data (160 test reviews)
y_train_new = labels
predictions_new=[]
kNearestNeighbor(X_train_tf, y_train_new, X_test_tf, predictions_new, 7)

predListDigit = []
labelListDigit = []
for p in range(len(predictions_new)):
    if(predictions_new[p][0][0]=='deceptive'):
        predListDigit.append('1')
    else:
        predListDigit.append('0')
    

with open('../TestOutput', 'w') as f:
    for item in predListDigit:
        f.write("%s\n" % item)