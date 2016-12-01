/**
 * Created by Administrator on 2016/7/18.
 */


function init() {
    $('.ui.menu>.item').on('click', function() {
        $(this).parent().find('.item').removeClass('active');
        $(this).addClass('active');
    });

    $('.dropdown').dropdown({action:'hide'});
    requestData();
}

function onSuccess(data) {
    alert(JSON.stringify(data));
}

function requestData() {
    $.ajax({
        url:"http://localhost:8001/role/get?id=3&jsonpCallback=onSuccess",
        dataType:"jsonp",
        success:function() {
            alert("ok");
        }
    });


}
