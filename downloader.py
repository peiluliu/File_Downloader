import threading
import requests
import time
import os
import argparse


class DownloadThread(threading.Thread):
    def __init__(self,url,startIndex,endIndex,f):
        super(DownloadThread,self).__init__()
        self.url = url
        self.startIndex = startIndex
        self.endIndex = endIndex
        self.subFile = f
    def run(self):
        headers = {'Range':'bytes=%s-%s'%(self.startIndex, self.endIndex)}
        try:
            res = requests.get(self.url, headers = headers)
        except requests.exceptions.Timeout:
            print('Download failed!')
            print('Time out error. Please check network connection and retry.')
        except requests.exceptions.TooManyRedirects:
            print('Download failed!')
            print('The URL is bad. Try another URL please.')
        except requests.exceptions.RequestException:
            print('Download failed!')
            print('Unable to make request!')

        self.subFile.seek(self.startIndex)
        self.subFile.write(res.content)
        self.subFile.close()


def startDownload(url, threadnum):  
        try: 
            filename = url.split('/')[-1] 
            if '.' not in filename:
                pass            
            filesize = int(requests.head(url).headers['Content-Length'])
            threading.BoundedSemaphore(10)
            if threadnum > 10:
                threadnum = 10
                print('Warning: Please enter no more than 10 threads!')
            subSize = filesize // threadnum
            print('The size of %s is: %s' %(filename,filesize))
            print('There are %s threads running to download %s.' %(threadnum, filename))
            startIndex = 0
            endIndex = -1
            subFileList = []
            download_path = '/'.join( os.getcwd().split('/')[:3] ) + '/Downloads/' 
            tempFile = open(download_path + filename, 'w')
            tempFile.close()
            f= open(download_path + filename, 'rb+')
            fileno = f.fileno()
            while endIndex < filesize -1:
                startIndex = endIndex + 1
                endIndex = startIndex + subSize - 1
                if endIndex > filesize:
                    endIndex = filesize
                dup = os.dup(fileno)
                subFile = os.fdopen(dup, 'rb+', -1)
                thread = DownloadThread(url, startIndex, endIndex, subFile)
                thread.start()
                subFileList.append(thread)
            for i in subFileList:
                i.join()
            f.close()
            print('Download success!')
        except requests.exceptions.Timeout:
            print('Download failed!')
            print('Time out error. Please check network connection and retry.')
        except requests.exceptions.TooManyRedirects:
            print('Download failed!')
            print('The URL is bad. Try another URL please.')
        except requests.exceptions.HTTPError:
            print('Download failed!')
            print ('URL not found. Please check your URL.')
        except requests.exceptions.RequestException:
            print('Download failed!')
            print('Unable to make request!')


if __name__=="__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("url")
    parser.add_argument("-c", "--multiThread",type=int,default=1)
    args = parser.parse_args()
    startDownload(url=args.url,threadnum=args.multiThread)
