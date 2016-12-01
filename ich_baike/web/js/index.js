/**
 * Created by Administrator on 2016/8/18.
 */

function init() {
    load_project();
}

function load_project() {
    $.ajax({
        url:'../artproject/getbystatus',
        data:{status:1, from:0, to:10},
        success:showProject
    })
}



function showProject(data) {
    window.projectList = data.data;
    if(typeof projectList != 'undefined' ) {
        $(".ui.cards").append(getHTMLByTemplate("projectList", projectList));
    }
    $(".heart.button").on('click', function () {
        if($(this).hasClass("loading")) {
            return;
        }
        if($(this).find('.icon').hasClass("empty")) {
            var projectId = $(this).parent().parent().attr('id');
            praise(projectId);
        } else {
            var praiseId = $(this).children(0).attr("id");
            unpraise(praiseId);
        }
    })
}

function praise(projectId) {
    var $button = $('#' + projectId + ' .heart.button');
    $button.addClass('disabled');
    $button.find('i').addClass('loading');
    $.ajax({
        url: '../praise/praiseproject',
        data:{projectid:projectId},
        success:function (obj) {
            $button.removeClass('disabled');
            $button.find('i').removeClass('loading');
            if(obj.code != 0) {
                console.log(obj.description);
                return;
            }
            var praise = obj.data;
            var count = Number($button.text()) + 1;
            var $icon = $('<i class="red heart icon"></i>');
            $icon.attr('id', praise.id);
            $button.text(count);
            $button.find('i').remove()
            $button.prepend($icon);
        }
    })
}

function unpraise(praiseId) {
    var $button = $('#' + praiseId ).parent();
    $button.addClass('disabled');
    $button.find('i').addClass('loading');
    $.ajax({
        url: '../praise/unpraiseproject',
        data:{praiseid:praiseId},
        success:function (obj) {
            $button.removeClass('disabled');
            $button.find('i').removeClass('loading');
            if(obj.code != 0) {
                return;
            }
            var count = Number($button.text()) -1;
            var $icon =$('<i class="red empty heart icon"></i>');
            $button.text(count);
            $button.find('i').remove()
            $button.prepend($icon);
        }
    })

}


