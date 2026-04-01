# game-of-life-gifs
### An implementation of Conway's Game of Life which produces .gifs as outputs.


The Game of Life (https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life) is a well loved set of rules which give rise to fascinating and beautiful patterns.
I wanted to have a way to examine the evolution of a game in a compact format. Due to the implicit time dependency of the game, I felt that gifs were a natural choice of medium.

This repository contains two main programs. The first, titled simply GOL, is a traditional implementation of this idea, albeit with simplified edge conditions (personal preference :) ). The other, GOLColors, uses three separate "channels" which are layered and assigned to RGB color space to create much more visually interesting displays. However, the actual mechanics at play there are the same, merely in triplicate; that is to say the three layers have no interaction with another, and are just superimposed for visual enjoyment.

This project was made possible by the work of Elliot Kroo, whose GifSequenceWriter is used for the output generation. This class is included for convenience. In addition, a number of my test inputs and outputs are included as examples.
