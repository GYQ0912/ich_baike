/**
 * Created by Administrator on 2016/9/5.
 */

$.ajaxSetup({
    dataFilter:function(data, type) {
        if(type == 'script') {
            return data;
        }
        var nofilter = this.nofilter;
        if(typeof nofilter != 'undefined' && nofilter) {
            return data;
        }
        var obj = $.parseJSON(data);
        var code = obj.code;
        if(typeof code == 'undefined' || code == "0") {
            return obj;
        }  else {
            $.info(obj.description);
            return obj;
        }
    },
    error: function(jqXHR, textStatus, errorThrown){
        switch (jqXHR.status){
            case(500):
                console.log("服务器系统内部错误");
                break;
            case(401):
                console.log("未登录");
                break;
            case(403):
                console.log("无权限执行此操作");
                break;
            case(408):
                console.log("请求超时");
                break;
            default:
                console.log("未知错误");
        }
        return false;
    }
});



$(function() {
    $.extend({
        info: function (message) {
            var template = '<div class="ui top fixed small info message">'
                + message
                + '</div>';
            var $bar = $(template);
            $('body').append($bar);
            var height = $bar.outerHeight();
            $bar.css('top', -height);
            $bar.animate({top: 0}, 300);
            $bar.animate({top:0}, 5000);
            $bar.animate({top:-height}, 300, function () {
                $bar.remove();
            });

        }

    });
    
    $.extend({
        randomString:function (len) {
            len = len || 32;
            var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
            var maxPos = $chars.length;
            var str = '';
            for (i = 0; i < len; i++) {
                str += $chars.charAt(Math.floor(Math.random() * maxPos));
            }
            return str;
        },
        translateCssTmp:'.translateion {-webkit-animation: translation  @duration@s ease-in-out;}  @-webkit-keyframes translation{0%   { -webkit-transform: translate(0px, 0px); } 100% { -webkit-transform: translate(@distance@px, 0px); } }'
    });

    $.fn.extend({
        tanslate:function (distance, second, callback) {
            var tmp = $.translateCssTmp;
            var css = tmp.replace('@duration@', second).replace('@distance@', distance);
            var $css = $('<style></style>');
            var id = $.randomString(8);
            $css.attr('id', id);
            $css.text(css);
            $('head').append($css);
            this.addClass('translateion');
            setTimeout(callback, second * 1000);
        }
    })

    $(document).on("DOMNodeInserted", function (e) {
        $(e.target).find('a').off('click');
        $(e.target).find('a').on('click', function(){
            var uri = $(this).attr("href");
            if(typeof uri != 'undefined') {
                loadPage(uri);
                return false;
            }
        })
    })

    function loadPage(uri, isBack) {
        $.ajax({
            url: uri + '?' + Math.random(),
            nofilter:true,
            success:function (data) {
                goto(data, uri, isBack);
            }
        })
    }

    function goto(data, uri, isBack) {
        window.inited = false;
        if(typeof window.pageStack == 'undefined' && !isBack) {
            window.pageStack = new Array();
        }
        window.pageStack.push(window.location.pathname);

        var $page = $(data);
        var $element_array = new Array();
        var $header_array = new Array();
        var $script_array = new Array();
        renderPage(data, $element_array, $header_array, $script_array);
        switchPage($element_array, isBack, function () {
            $('head>*').remove();
            loadPageScript($script_array);
            loadHead($header_array);
        });

    }

    function renderPage(data, $element_array,$header_array, $script_array ) {
        $page = $(data);
        for(var i = 0; i < $page.length; i++) {
            var $element = $page.eq(i);
            if($element.is('meta') || $element.is('style') || $element.is('link') || $element.is('title')) {
                $header_array.push($element);
            } else if($element.is('script') && typeof $element.attr('src') != 'undefined') {
                $script_array.push($element.attr('src'));
            }  else {
                $element_array.push($element);
            }
        }
    }

    function switchPage($element_array, isBack, callback) {
        var $container_old = $('<div></div>');
        $container_old.append($('body').children());
        $('body').append($container_old);
        var body_width = $container_old.width();

        var $container_new = $('<div></div>');
        $container_new.append($element_array);
        $container_old.css('position', 'absolute').css("width", body_width);
        $container_new.css('position', 'absolute').css('width',body_width);

        var $container = $('<div></div>');
        //$container.css('width', body_width).css('position', 'relative').css('overflow', 'hidden')
        //    .css('height', $container_old.outerHeight());
        //$container.append($container_old).append($container_new).appendTo($('body'));
        //var direction = isBack?-1:1;
        //$container_old.find('.fixed').css('left', 'absolute');
        $container_old.tanslate(-body_width, 0.4, function () {
            $container_old.remove();
        });
        $('body').append($container_new);
        $container_new.css('left', body_width);
        $container_new.translate(-body_width, 0.4, function () {
            $container_new.css('left', 0);
        })

        /*
        $container_new.css("left", direction * body_width).animate({left:0}, 400, function() {
            $('body').append($container_new.children());
            $container.remove();
            callback();
            $('.back').on('click', function () {
                loadPage(pageStack.pop(), true);
            })
            
        });*/
    }

    function loadPageScript(script_array) {
       for(var i = 0; i < script_array.length; i++) {
           var $script = $('<script src="' + script_array[i] + '"></script>');
           $('head').append($script);

       }

    }
    window.inited = false;
    try {
        init();
        window.inited = true;
    } catch (error) {
        console.log(error);
    }

    function loadHead(head_array) {
        for(var i = 0; i < head_array.length; i++) {
            $('head').append($(head_array[i]));
        }
    }

});




