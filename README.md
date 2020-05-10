# Visualizer & Debugger for Comp250 Assignment 3 @ McGill University
![screenshot1](https://sashaphotoca.files.wordpress.com/2020/04/2020-04-18-23_39_23-cviz.png)
![screenshot2](https://sashaphotoca.files.wordpress.com/2020/05/96267004_1575324725949752_5924019537782505472_n.jpg)

# What does it do
This tester provides a visual interface for interacting with your binary tree. More feaures for automated testing to be added. Have fun!

# Installation

## Installation: Standard Method

Clone this repository to your directory of choice.
Add your assignment .java files <code>CatTree.java, CatInfo.java</code> and any of your own classes to the <code>COMP250_A3_W2020</code> package, in the location of <code>Put Your java files here.txt</code>

![IntelliJ Recommended](https://raw.githubusercontent.com/TheBigSasha/C250_Assignment3_Debugger/master/IntelliJ%20Recommended.jpg)
![Eclipse Recommended](https://raw.githubusercontent.com/TheBigSasha/C250_Assignment3_Debugger/master/EclipseRecommended.jpg)

## Installation: Submodule Method

*This method is useful if you are managing your own git repository for this project and do not want to fork this one.* It allows you to maintain your existing workflow and still keep up to date with new versions of the visualizer.

IntelliJ Idea: Right click your project folder -> open in terminal -> enter command: <code>git submodule add https://github.com/TheBigSasha/C250_Assignment3_Debugger</code>

Other IDE on Linux / Mac: Open terminal. CD your project directory, enter command: <code>git submodule add https://github.com/TheBigSasha/C250_Assignment3_Debugger</code>

Other IDE on Windows: [Install Git Bash](https://gitforwindows.org/) Follow steps for Linux/Mac using Git Bash.


### Note Regarding installation:
It is important that you use this repository as the folder structure for use of this debugger. It may work if you drag <code>CViz.java, RandomCats.java</code> and associated .form files into your project, but it is not likely to work across both IntelliJ and Eclipse.
 
# How to Contribute

Want to contribute to the project? Feel free to [fork this repository](https://help.github.com/en/github/getting-started-with-github/fork-a-repo) and add whatever you like. Pull request and chances are that if your code is cool, it'll be merged :)

For any questions, contact me at sasha@sashaphoto.ca

# Visualization algorithm:

### In order to visualize this data structure, the basic algorithm is:
Traverse the data structure, upon each traversal to the "next" node, draw a representation of the node on screen with a certain offset and display some information.

### Applied to the bTree, this algorithm looks like:
Start tree traversal at a node.
Traverse through the subtrees, keeping track of pixel offset on each recursion.
Draw a line connecting a node to the node being recursed to.
Represent the linkedlist stored in each node with a visualization following loosely the visualization algorithm from assignment 2.

### Possible improvements:
#### Auto scaling:
before initial traversal, calculate width and height of tree and scale offset factors to fill the window size for any tree.
#### Alternative offsets:
Use non-linear / non-randomized offets of some kind to reduce overlap with dense trees.
#### In-operation visualization:
Visualize the tree traversal by highlighting the "current" node as in the A2 debugger. This would require access/modificiation to assignment source code.
