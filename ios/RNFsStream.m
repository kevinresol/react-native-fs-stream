
#import "RNFsStream.h"

@implementation RNFsStream

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

- (void) open:(NSString *)path
         flag:(int)flag
     resolver:(RCTPromiseResolveBlock)resolve
     rejecter:(RCTPromiseRejectBlock)reject
{
}

- (NSString *)getPathForDirectory:(int)directory
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(directory, NSUserDomainMask, YES);
    return [paths firstObject];
}

- (NSDictionary *)constantsToExport
{
    return @{
             @"MainBundlePath": [[NSBundle mainBundle] bundlePath],
             @"CachesDirectoryPath": [self getPathForDirectory:NSCachesDirectory],
             @"DocumentDirectoryPath": [self getPathForDirectory:NSDocumentDirectory],
             @"ExternalDirectoryPath": [NSNull null],
             @"ExternalStorageDirectoryPath": [NSNull null],
             @"TemporaryDirectoryPath": NSTemporaryDirectory(),
             @"LibraryDirectoryPath": [self getPathForDirectory:NSLibraryDirectory],
             @"FileTypeRegular": NSFileTypeRegular,
             @"FileTypeDirectory": NSFileTypeDirectory
             };
}

RCT_EXPORT_METHOD(openForRead:(NSString *)path
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    
    int fd = open([path UTF8String], O_RDONLY);
    if(fd == -1) {
        NSError *error = [[NSError alloc] initWithDomain:NSPOSIXErrorDomain code:404 userInfo:nil];
        reject(@"Not Found", @"File not found", error);
    }
    
    resolve([NSNumber numberWithInt:fd]);
}

RCT_EXPORT_METHOD(openForWrite:(NSString *)path
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    
    int fd = open([path UTF8String], O_WRONLY | O_CREAT, 0666);
    if(fd == -1) {
        NSError *error = [[NSError alloc] initWithDomain:NSPOSIXErrorDomain code:404 userInfo:nil];
        reject(@"Not Found", @"File not found", error);
    }
    
    resolve([NSNumber numberWithInt:fd]);
}

RCT_EXPORT_METHOD(read:(int)fd size:(int)size
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSMutableData *data = [[NSMutableData alloc] initWithLength:size];
    void *buffer = [data mutableBytes];
    int offset = 0;
    int numRead = 0;
    
    while(offset < size && (numRead = read(fd, buffer, size - offset)) > 0) {
        offset += numRead;
    }
    
    if(numRead == -1) {
        NSError *error = [[NSError alloc] initWithDomain:NSPOSIXErrorDomain code:errno userInfo:nil];
        reject([NSString stringWithUTF8String:strerror(errno)], @"Cannot read from file", error);
        return;
    }
    
    if(numRead <= 0) {
        close(fd);
    }
    
    if(offset < size) {
        [data setLength:offset];
    }
    
    NSMutableDictionary *result = [NSMutableDictionary dictionary];
    [result setObject:[data base64EncodedStringWithOptions:0] forKey:@"data"];
    [result setObject:[NSNumber numberWithInt:offset] forKey:@"bytesRead"];
    [result setObject:[NSNumber numberWithBool:numRead == 0] forKey:@"ended"];
    resolve(result);
    
}

RCT_EXPORT_METHOD(write:(int)fd data:(NSString *)str
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSData *data = [[NSData alloc] initWithBase64EncodedString:str options:0];
    int ret = write(fd, [data bytes], [data length]);
    if(ret == -1) {
        NSError *error = [[NSError alloc] initWithDomain:NSPOSIXErrorDomain code:500 userInfo:nil];
        reject(@"Write Error", @"Cannot write to the specified file descriptor", error);
    } else {
        resolve([NSNull null]);
    }
}


RCT_EXPORT_METHOD(closeRead:(int)fd
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    close(fd);
    resolve([NSNull null]);
}

RCT_EXPORT_METHOD(closeWrite:(int)fd
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    close(fd);
    resolve([NSNull null]);
}

@end
  
