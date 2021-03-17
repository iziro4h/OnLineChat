let myUserName

function randomString(len) {
    len = len || 32;
    let $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
    let maxPos = $chars.length;
    let str = '';
    for (let i = 0; i < len; i++) str += $chars.charAt(Math.floor(Math.random() * maxPos));
    return str;
}

let submitMessage = function () {
    let message = $('#userMessage').val()
    $.ajax({
        url: '/chat/pushMessage',
        type: 'POST',
        data: JSON.stringify({
            user: myUserName,
            msg: message
        }),
        contentType: 'application/json',
        success: r => {
            // console.log(r)
            let id = randomString(16)
            $('#message').append('<div class="msg-from-me">' +
                '<div style="margin-left: auto">' +
                '<div class="msg-user-code" style="text-align: end">' + myUserName + '</div>' +
                '<div id="' + id + '" class="msg-user-message"></div>' +
                '</div>' +
                '<div class="head-css">' + myUserName.substring(0, 1) + '</div>' +
                '</div>')
            $('#' + id).text(message)
            $('#userMessage').val('')
            let div = document.getElementById('message')
            div.scrollTop = div.scrollHeight
        },
        error: e => {
            console.error(e)
        }
    })
}

let messageHandler = function (msg) {
    // console.log(msg)
    let obj
    switch (msg.type) {
        case 'sys_tip':
            obj = msg.data
            if (myUserName == obj.user) break
            let dao = 'in' == obj.dao ? '进入' : ('out' == obj.dao ? '退出' : '')
            if (dao) $('#message').append('<div class="msg-tips"><span style="color: mediumspringgreen">' + obj.user + '</span>&nbsp;' + dao + '了群聊</div>')
            break
        case 'msg':
            obj = msg.data
            if (myUserName == obj.user) break
            let id = randomString(16)
            $('#message').append('<div class="msg-from-user">' +
                '<div class="head-css">' + obj.user.substring(0, 1) + '</div>' +
                '<div>' +
                '<div class="msg-user-code">' + obj.user + '</div>' +
                '<div id="' + id + '" class="msg-user-message"></div>' +
                '</div>' +
                '</div>')
            $('#' + id).text(obj.msg)
            break
        case 'sys_dao':
            let users = msg.data.lines
            let html = '<span style="border-bottom: 1px solid mediumspringgreen">当前在线：<span>' + users.length + '</span>人</span>'
            users.forEach(v => {
                html += '<div class="online-item">' +
                    '<div>' + v + '</div>' +
                    '<div>已连接<div class="green-point"></div>' +
                    '</div>' +
                    '</div>'
            })
            $('#now_online_per').html(html)
            break
    }
    let div = document.getElementById('message')
    div.scrollTop = div.scrollHeight
}


let initMySocket = function (user) {
    let ws
    if (!ws) {
        try {
            ws = new WebSocket('ws://121.199.22.66:801/chat/' + user);//实际服务器地址
            // ws = new WebSocket('ws://localhost:801/chat/' + user);//本地测试连接服务器
            ws.onopen = function (ev) {
                // console.log('onopen', ev)
            }
            ws.onmessage = function (ev) {
                // console.log('onmessage', ev)
                messageHandler(JSON.parse(ev.data))
            }
            ws.onclose = function (ev) {
                // console.log('onclose', ev)
            }
            ws.onerror = function (ev) {
                // console.log('onerror', ev)
            }
        } catch (ex) {
            alert(ex.message)
        }
    } else {
        ws = null
    }
}

window.onload = function () {
    //初始化自己的角色名字
    myUserName = $('#userShowName').val()
    initMySocket(myUserName)
    $('#submitMsg').click(submitMessage)
}

window.onkeydown = ev => {
    if (ev.ctrlKey && ev.keyCode == 13) submitMessage()
}
