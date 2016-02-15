# durable-cells
Local Storage for Hoplon

[![Clojars Project](https://img.shields.io/clojars/v/aatree/durable-cells.svg)](https://clojars.org/aatree/durable-cells)

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

**```(open-durable-cells! {"txt" txt})```**

Opens the database ```"durable-cells"```, loads the cells in the dictionary parameter
and then watches them--saving any changes to the database.

The keys in the dictionary are the names of the cells in the database;
the values in the dictionary are the cells.

**```(defc durable-cells/ready false)```**

After all the cells included in the open-durable-cells dictionary parameter
have been loaded, ```ready``` is reset! to true.

**```(defc durable-cells/error nil)```**

The error cell is reset! when an error occurs.

## Change Log

**0.1.0** - Rework the API. Eliminate the need for application worker-side code.
Signal ready only after loading completes.

**0.0.1** - Initial release.
