
# react-native-fs-stream

## Getting started

`$ npm install react-native-fs-stream --save`

### Mostly automatic installation

`$ react-native link react-native-fs-stream`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-fs-stream` and add `RNFsStream.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNFsStream.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNFsStreamPackage;` to the imports at the top of the file
  - Add `new RNFsStreamPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-fs-stream'
  	project(':react-native-fs-stream').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-fs-stream/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-fs-stream')
  	```

## Usage
```javascript
import FsStream from 'react-native-fs-stream';

const path = Fs.DocumentDirectoryPath + '/test.txt';
Fs.openForWrite(path).then((fd) => {
	return Fs.write(fd, 'MTIzNDU2Nzg5MA==')
		.then(() => {
			Fs.closeWrite(fd);
			return Fs.openForRead(path);
		});
}).then((fd) => {
	const self = this;
	function next() {
	Fs.read(fd, 3).then((v) => {
		console.log(v);
		self.setState({data: self.state.data + v.data});
		if(!v.ended) next();
	});
	}
	next();
}).catch((e) => console.log(e));
```


## API

```haxe
/** Open a file for read. Returns a file descriptor. Note that on android it is a psuedo-fd **/
function openForRead(path:String):Promise<Int>;
/** Open a file for write. Returns a file descriptor. Note that on android it is a psuedo-fd **/
function openForWrite(path:String):Promise<>;
/** Read some bytes from a fd, returns the data as base64 encoded string **/
function read(fd:Int, size:Int):Promise<{data:String, bytesRead:Int, ended:Bool}>;
/** Read some data(base64 encoded) to a fd. **/
function write(fd:Int, data:String):Promise<Void>;
/** Close read **/
function closeRead(fd:Int):Promise<Void>;
/** Close write **/
function closeWrite(fd:Int):Promise<Void>;
```
  
