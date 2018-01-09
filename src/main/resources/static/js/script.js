var stompClient = null;
var connected = false;
var content = $("#chatcontent");
var cursor = null;
var username = null;
function connect() {
    username = localStorage.getItem("username");
    stompClient = Stomp.over(new SockJS('/message'));
    stompClient.connect({}, function (frame) {
        connected = true;
        //console.log('Connected: ' + frame);
        stompClient.subscribe('/clients/message', function (message) {
            //console.log(message);
            var response = JSON.parse(message.body);
            if(response.id < 0){
                if(response.from===username){
                    showGreeting(response.content);
                }else{
                    receiveMessage("chat:"+JSON.parse(message.body).content+"from:"+JSON.parse(message.body).from);
                }

            }else{
                var data = JSON.parse(message.body).content;
                var lines = "";
                for(var i=0;i<data.length;i++){
                    lines += data[i];
                }
                editor.getDoc().setValue(lines);
                //重置光标位置
                editor.doc.setCursor(cursor);
            }
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendName(message) {
    stompClient.send("/server/message", {}, JSON.stringify({'id':-1,'content': message}));
}

function showGreeting(msg) {
    content.append("<div class=\"customer_lists clearfix\"><div class=\"header_img jimi3\" style=\"background: url(../img/mine.jpg) no-repeat center;\"><div class=\"header_img_hover\"></div></div><div class=\"bkbubble left\"><p>"+msg+"</p></div></div>");
    content.scrollTop(content[0].scrollHeight);
}

function sendMessage(msg){
    var input =$('#inputBox');
    if(!connected){
        connect();
    }
    sendName(input.val());
    input.val("");
}

function receiveMessage(msg){
    content.append("<div class=\"jimi_lists clearfix\"><div class=\"header_img jimi3 fl\"></div><div class=\"bkbubble right\"><p>"+msg+"</p></div></div>");
    content.scrollTop(content[0].scrollHeight);
}
var editor = CodeMirror.fromTextArea(document.getElementById("codemirror"), {
    lineNumbers: true,
    mode: "text/x-java"
});

var map = {"Ctrl-S": function(cm){update();}}
editor.addKeyMap(map);

var mac = CodeMirror.keyMap.default == CodeMirror.keyMap.macDefault;
CodeMirror.keyMap.default[(mac ? "Cmd" : "Ctrl") + "-Space"] = "autocomplete";

$.post("/file",function(data){
    editor.getDoc().setValue(mtoString(data));
});

function mtoString(data){
    var lines = "";
    for(var i=0;i<data.length-1;i++){
        lines += data[i]+"\n";
    }
    lines +=data[i];
    return lines;
}

function update(){
    var content = editor.getValue();
    $.post("/update",{data:content,path:"/cooperativecoding/hello.c"}, function(data) {
        console.log(data);
    });
}

function run(){
    $.post("/run",function(data) {
        $("#console").val(data);
    });
}

function resize(){
    var width = document.body.clientWidth;
    var height = document.body.clientHeight;
    $("body").height(height-60);
    editor.setSize(width*0.7+10,height-180);
    content.height(height-120);
}

$(document).ready(function(){
    connect();
    resize();

    content.scrollTop(content[0].scrollHeight);
});

window.onresize = function resizeBody(){

    console.log("document.body.clientHeight =  "+document.body.clientHeight);
    console.log("document.body.clientWidth =  "+document.body.clientWidth);
    resize();
};

//阻止内容相同时触发的change事件，防止死循环
editor.on('beforeChange',function (cm,obj) {
    cursor = cm.doc.getCursor();
    if(cm.getValue()===mtoString(obj.text)){
        obj.cancel();
    }
});

//监听编辑器代码变动，同步到所有客户端
editor.on('change',function (cm) {
    if(connected){
        stompClient.send("/server/message",{},JSON.stringify({'id':7,'content':cm.getValue()}));
    }
});

//切换代码高亮模式
CodeMirror.modeURL = "js/codemirror/mode/%N/%N.js";
var modeInput = document.getElementById("mode");
CodeMirror.on(modeInput, "keypress", function(e) {
    if (e.keyCode == 13) change();
});
function change() {
    var val = modeInput.value, m, mode, spec;
    if (m = /.+\.([^.]+)$/.exec(val)) {
        var info = CodeMirror.findModeByExtension(m[1]);
        if (info) {
            mode = info.mode;
            spec = info.mime;
        }
    } else if (/\//.test(val)) {
        var info = CodeMirror.findModeByMIME(val);
        if (info) {
            mode = info.mode;
            spec = val;
        }
    } else {
        mode = spec = val;
    }
    if (mode) {
        editor.setOption("mode", spec);
        CodeMirror.autoLoadMode(editor, mode);
    } else {
        alert("Could not find a mode corresponding to " + val);
    }
}