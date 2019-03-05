I have not enough time to check this code and make this more clearly.
So, I used System.out  instead log, do not check all input parameters,
some name value may sound very strange.
//todo unit tests
About program 3 parameters on input, if it is 2, id will be set by default.

work algoritm

1. Find node by id. I use Xpath. Create map with attribute, value, parameters
It is base element.
2. Search by tag in changing html(sample). ie in our case search will
 by tag 'a'. I add additional search by tag 'button' for test case 4.
 We also have map with attribute, value, parameters for each found element.
3.Compare element that we found in p1  with elements get from p2 by compare criteria.

4.Element is found when all criteria in criteria slot be successful.
//todo put criteria to file
for sample: tag, path to element and trim text one element equals to
tag, path to element and trim text another element

5. result diff return to standart out.
Sample result you may see in file result.txt
//todo put diff to file

as use

Sample start you may find in file start jar file, suppused that 'input' is
folder where testcase plased.
After start you may see result on console.

