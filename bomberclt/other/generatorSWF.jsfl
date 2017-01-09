var filepas = "file:///c|/work/bomber/swf/";
FLfile.createFolder(filepas);

for (var i in  fl.documents){
	fl.trace ("Generate SWF By: "+fl.documents[i].name.split(".")[0]);
	start(fl.documents[i]);
}

function start(){
	var layers = document.getTimeline().layers;	
	for (i = layers.length - 1; i >= 0; i--){
		var layer  = layers[i];
		if (layer.layerType == "normal"){			
			var frames = layer.frames;
			for (var j = 0; j < frames.length; j++){
				var frame = frames[j];
				var elements = frame.elements;
				for (var k = 0; k < elements.length; k++){
					fl.trace("======> " + elements[k].libraryItem.name);
					elements[k].libraryItem.exportSWF(filepas + elements[k].libraryItem.name + ".swf");
				} 
			}
		}
	}
	fl.trace("Generate complete");
}