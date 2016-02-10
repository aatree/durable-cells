# durable-cells
Local Storage for Hoplon

1. [Introduction](#introduction)
1. [API](#api)
1. [Change Log](#change-log)

## Introduction

You can do a lot on a web page when you have local storage in the browser.
And that is exactly what
[IndexedDb](https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB_API)
gives you.

IndexedDb is just the perfect thing for saving the contents of 
[Hoplon/Javelin](https://github.com/hoplon/javelin)
cells between sessions.
There are a few obstacles, like running IndexedDb in a 
[web worker](http://www.w3schools.com/html/html5_webworkers.asp)
and getting IndexedDb to accept 
[EDN](https://github.com/edn-format/edn)
rather than json. But these are
easy enough to overcome with a bit of code.

demo: [Duracell](https://github.com/aatree/aademos/tree/master/duracell)

## API

**```(durable-cells.core/start "my-cells")```**

Opens the database ```"my-cells"```. On successful completion,
```aaworker.api/process-requests``` is called.

**```(durable-cells/load-cell success failure cell-name)```**

Returns the previously saved value of the cell, or ```nil```.
The ```success``` parameter is a function which is called with the 
result of a successful get.
And the ```failure``` cell is a function which is called with the
error of a failed get.

**```(durable-cells/save-cell success failure cell-name value)```**

Similar to ```load-cell```, except that the value is put in the cell.
And on successful completion of the put, the cell-name is returned
via the ```success``` function.

## Change Log

**0.0.1** - Initial release.
