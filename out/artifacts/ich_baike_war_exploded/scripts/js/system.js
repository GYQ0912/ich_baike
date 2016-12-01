/**
 * Created by Administrator on 2016/7/21.
 */

//页面切换
(function(){
    var oMenu=$('.swiper-menu span,.com-nav .swiper-menu .item .line');
    oMenu.on('click mousedown',function(){
        $(this).addClass('active').siblings('span').removeClass('active');
        swiperContainer.slideTo($(this).index());
    });
    var swiperContainer=new Swiper('.swiper-container',{
        speed:500,
        autoHeight: true,
        onSlideChangeStart:function () {
            oMenu.eq(swiperContainer.activeIndex).addClass('active').siblings('span').removeClass('active');
        }
    });
})();

//融资详情
(function () {
    $('.details-rz .project .users dt .button').click(function () {
        $(this).toggleClass('active');
        $(this).find('.heart').toggleClass('empty');
    })
})();
//主页限制字数
(function(){
    function txtT(id,len){
        var Id = $(id)
        Id.each(function(){
            var txt = $(this).text().length;//获取当前容器的字符数
            if(txt>len){
                $(this).text($(this).text().substr(0,len)).append("...")
            }
        })
    }
    txtT(".lh-list li .h-txt p",18)//艺术家主页，信息内容限制
    txtT(".formerly li .img-works .txt-works p",20)//投过的，信息内容限制
})();