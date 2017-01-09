var filepas = "file:///c|/work/bomber";

var lastID;
var uin = 0;
var globallibs = new Object ();
var ids;
var errors = new Array ();
var faces = new Object ();
var exportSWF = false;

for (var i in  fl.documents){
	fl.trace ("-------------->  START "+fl.documents[i].name.split(".")[0]);
	start (fl.documents[i]);
}
if (errors.length > 0){
	fl.trace("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	fl.trace("++++++++++++++++  ERROR  ++++++++++++++++++++++  ERROR  ++++++++++++++++");
	fl.trace("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	for (var i = 0;i < errors.length;i++){
		traceError (errors[i].txt,errors[i].file);
	}
}


function start(){
	var layers = document.getTimeline().layers;
	var xml = "<scene>\r";
	//var xml = "<?xml version='1.0' encoding='UTF-8'?>\r<scene>\r"
	
	for (i = layers.length - 1; i >= 0; i--){
		var layer  = layers[i];		
		if (layer.layerType == "normal"){			
			var frames = layer.frames;
			for (var j = 0; j < frames.length; j++){
				var frame = frames[j];
				var elements = frame.elements;
				for (var k = 0; k < elements.length; k++){
					var element = elements[k];					
					if (element.libraryItem){
						if(element.libraryItem.name.toLowerCase() == "carriervertical" ||
							element.libraryItem.name.toLowerCase() == "carrierhorizontal"){
							
							if(element.libraryItem.name.toLowerCase()  == "carriervertical") xml += "\t<" + "carrv";
							if(element.libraryItem.name.toLowerCase()  == "carrierhorizontal") xml += "\t<" + "carrh";							
							
							var framecarrier = element.libraryItem.timeline.layers[0].frames[0];
							for (var f = 0; f < framecarrier.elements.length; f++){
								var elementcarrier = framecarrier.elements[f];
								if (elementcarrier.libraryItem){
									if(elementcarrier.libraryItem.name.toLowerCase() == "staticblack"){										
										xml += " bw='" + elementcarrier.width + "'";
										xml += " bh='" + elementcarrier.height + "'";
									}else if(elementcarrier.libraryItem.name.toLowerCase() == "prismaticline"){
										xml += String(" x='" + (element.x + elementcarrier.x) + "'");
										xml += String(" y='" + (element.y + elementcarrier.y) + "'");
										
										xml += " lw='" + elementcarrier.width + "'";
										xml += " lh='" + elementcarrier.height + "'";
									}									
								}
							}
							xml += "/>\r";						
						}else{						
							if(element.libraryItem.name.toLowerCase()  == "box") xml += "\t<" + "box";
							else if(element.libraryItem.name.toLowerCase()  == "bullet") xml += "\t<" + "bull";
							else if(element.libraryItem.name.toLowerCase()  == "source") xml += "\t<" + "src";
							else if(element.libraryItem.name.toLowerCase()  == "heavybox") xml += "\t<" + "hbox";
							else if(element.libraryItem.name.toLowerCase()  == "hero") xml += "\t<" + "hero";
							else if(element.libraryItem.name.toLowerCase()  == "springboard") xml += "\t<" + "sprb";
							else if(element.libraryItem.name.toLowerCase()  == "staticblack") xml += "\t<" + "sbla";
							else if(element.libraryItem.name.toLowerCase()  == "staticblue") xml += "\t<" + "sblu";
							else if(element.libraryItem.name.toLowerCase()  == "staticred") xml += "\t<" + "sred";
							else if(element.libraryItem.name.toLowerCase()  == "static") xml += "\t<" + "stat";
							else if(element.libraryItem.name.toLowerCase()  == "stick") xml += "\t<" + "stik";
							else if(element.libraryItem.name.toLowerCase()  == "ball") xml += "\t<" + "ball";
							else xml += "\t<" + element.libraryItem.name.toLowerCase();
							
							xml += " x='" + element.x + "'";
							xml += " y='" + element.y + "'";
							xml += " w='" + element.width + "'";
							xml += " h='" + element.height + "'";
							xml += "/>\r";
						}
					}
				} 
			}
		}
	}
	xml += "</scene>";
	FLfile.createFolder(filepas);
	FLfile.write(filepas+"/"+document.name+".xml",xml);
	fl.trace("->* " + xml);
}


function traceError (txt,file){
	fl.trace (file +" -----> "+txt);
}