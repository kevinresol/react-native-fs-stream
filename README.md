
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

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNFsStream.sln` in `node_modules/react-native-fs-stream/windows/RNFsStream.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Cl.Json.RNFsStream;` to the usings at the top of the file
  - Add `new RNFsStreamPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNFsStream from 'react-native-fs-stream';

// TODO: What do with the module?
RNFsStream;
```
  