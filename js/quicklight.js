/**@author: Anas Elmoutaoaukil
This is a quick syntax highlighter that i'll use to higlight my preview code
feel free to copy it re use it
P.S i'm making this quickly so it may have a lot of errors and lack of refactoring , BE CAREFUL!
**/

var classes = [];
var keywords = [];
var comments = [];
var bracesAndOperators = [];
var fields = [];
var methods = [];

function LoadArrays()
{

  console.log("Loading Arrays");
  keywords.push("import");
  keywords.push("public");
  keywords.push("abstract");
  keywords.push("private");
  keywords.push("class");
  keywords.push("static");
  keywords.push("extends");
  keywords.push("implements");
  keywords.push("final");
  keywords.push("int");
  keywords.push("float");
  keywords.push("double");
  keywords.push("long");
  keywords.push("boolean");
  keywords.push("null");
  keywords.push("void");
  keywords.push("return");
  keywords.push("if");
  keywords.push("else");
  keywords.push("new");
  keywords.push("super");
  keywords.push("this");
  keywords.push("true");
  keywords.push("false");
  keywords.push("try");
  keywords.push("catch");
  keywords.push("synchronized");

  classes.push("System");
  classes.push("String");
  classes.push("android");
  classes.push("PopUpMenu");
  classes.push("PopUpMenuItem");
  classes.push("content");
  classes.push("Context");
  classes.push("graphics");
  classes.push("RectF");
  classes.push("Rect");
  classes.push("View");
  classes.push("view");
  classes.push("MotionEvent");
  classes.push("util");
  classes.push("os");
  classes.push("Handler");
  classes.push("AttributeSet");
  classes.push("Region");
  classes.push("BitmapDrawable");
  classes.push("Bitmap");
  classes.push("drawable");
  classes.push("Drawable");
  classes.push("Canvas");
  classes.push("Path");
  classes.push("Paint");
  classes.push("java");
  classes.push("ArrayList");
  classes.push("List");
  classes.push("Behavior");
  classes.push("IUIThreadFriendly");
  classes.push("IStateChangable");
  classes.push("ViewMeasureProperty");
  classes.push("FPSLooper");
  classes.push("ISuspendable");
  classes.push("Thread");
  classes.push("Runnable");
  classes.push("InterruptedException");
  classes.push("Exception");
}
function SearchArray(arr,str,tag)
{
  for(i = 0;i<arr.length;i++)
  {

    var regx = new RegExp("\\s"+arr[i]+"\\s","g");
    str = str.replace(regx," <"+tag+">"+arr[i]+"</"+tag+"> ");

    var regx = new RegExp(arr[i]+"\\s","g");
    str = str.replace(regx,"<"+tag+">"+arr[i]+"</"+tag+"> ");

    regx = new RegExp("\\s"+arr[i]+"\\(","g");
    str = str.replace(regx," <"+tag+">"+arr[i]+"</"+tag+">(");

    regx = new RegExp("\\s"+arr[i]+"\\;","g");
    str = str.replace(regx," <"+tag+">"+arr[i]+"</"+tag+">;");

    regx = new RegExp("\\}"+arr[i],"g");
    str = str.replace(regx,"}<"+tag+">"+arr[i]+"</"+tag+">");

    regx = new RegExp("[(]"+arr[i]+"\\s","g");
    str = str.replace(regx,"(<"+tag+">"+arr[i]+"</"+tag+"> ");

    regx = new RegExp("\\,"+arr[i],"g");
    str = str.replace(regx,",<"+tag+">"+arr[i]+"</"+tag+">");

    regx = new RegExp("\\="+arr[i],"g");
    str = str.replace(regx,"=<"+tag+">"+arr[i]+"</"+tag+">");

    regx = new RegExp("\\."+arr[i]+"\\.","g");
    str = str.replace(regx,".<"+tag+">"+arr[i]+"</"+tag+">.");

    regx = new RegExp("\\s"+arr[i]+"\\.","g");
    str = str.replace(regx," <"+tag+">"+arr[i]+"</"+tag+">.");

    regx = new RegExp("\\."+arr[i],"g");
    str = str.replace(regx,".<"+tag+">"+arr[i]+"</"+tag+">");


    regx = new RegExp("\\s"+arr[i]+"\\{","g");
    str = str.replace(regx," <"+tag+">"+arr[i]+"</"+tag+">{");

    regx = new RegExp(arr[i]+"&lt;","g");
    str = str.replace(regx,"<"+tag+">"+arr[i]+"</"+tag+">&lt;");

    regx = new RegExp(arr[i]+"&gt;","g");
    str = str.replace(regx,"<"+tag+">"+arr[i]+"</"+tag+">&gt;");

    regx = new RegExp(arr[i]+"\\)","g");
    str = str.replace(regx,"<"+tag+">"+arr[i]+"</"+tag+">)");
  }
  return str;
}
function searchKeyWords(str)
{
  return SearchArray(keywords,str,"kwrd");
}
function searchClasses(str)
{
  return SearchArray(classes,str,"clss");
}
function searchStr(str,startIndex)
{
  var index = str.indexOf("\"",startIndex);
  if(index == -1)
    return str;
  var index2 = str.indexOf("\"",index+1);
  if(index2 == -1)
  return str;
  str = str.slice(0,index)+"<str>"+str.slice(index,index2+1)+"</str>"+str.slice(index2+1,str.length);
  return searchStr(str,index2+6);// + 6 including tag length
}
function searchLineComments(str,startIndex)
{
  var index = str.indexOf("//",startIndex);
  if(index == -1)
    return str;
  var index2 = str.indexOf("\n",index+1);
  str = str.slice(0,index)+"<lcmt>"+str.slice(index,index2+1)+"</lcmt>"+str.slice(index2+1,str.length);
  return searchLineComments(str,index2+6);// + 6 including tag length
}
function searchString(str)
{
  str = searchClasses(str);
  str = searchKeyWords(str);
  str = searchStr(str,0);
  str = searchLineComments(str,0);

  return str;
}
function DoHighlight()
{
  /*So the highlighting process will be the following for eah <pre>
  *#1 we select an array
  *#2 we fetch next word [from start index to next space]
  *#3 we pick the color based on test result
  *#4 we wrap the word with the corresponding tag and then the css will take care*/

  var pres = document.getElementsByTagName("pre");
  for(i = 0;i<pres.length;i++)
  {
    pres[i].innerHTML = searchString(pres[i].innerHTML);
  }
}
function UnloadArrays()
{
  keywords = [];
  classes = [];
}
