function UUID(){}UUID.generate=function(){var a=UUID._gri,b=UUID._ha;return b(a(32),8)+"-"+b(a(16),4)+"-"+b(16384|a(12),4)+"-"+b(32768|a(14),4)+"-"+b(a(48),12)};UUID._gri=function(a){return 0>a?NaN:30>=a?0|Math.random()*(1<<a):53>=a?(0|1073741824*Math.random())+1073741824*(0|Math.random()*(1<<a-30)):NaN};UUID._ha=function(a,b){for(var c=a.toString(16),d=b-c.length,e="0";0<d;d>>>=1,e+=e)d&1&&(c=e+c);return c};

function getNewConversationId(){	return UUID.generate();};
function getConversationId(msg){	var myObject = JSON.parse(msg);	return myObject["conversation_id"];};//content parameter must be a serialized arrayfunction createJSONObject(agent_name, conversation_id, content){	var aux={};	aux["agent_name"]=agent_name;	aux["conversation_id"] = conversation_id;
	aux["content"] = {};	for(i in content){
		aux["content"][content[i].name] = content[i].value;	};	res = JSON.stringify(aux);			return res;};function createJSONObject(agent_name, content){	var aux={};	aux["agent_name"]=agent_name;	aux["conversation_id"] = UUID.generate();
	aux["content"] = {};	for(i in content){
		aux["content"][content[i].name] = content[i].value;	};	res = JSON.stringify(aux);			return res;};
