function createUrl() {
    var result = webContext;
   for (var i = 0; i < arguments.length; i++) {
      result += arguments[i];
   }
   result = result.replace('//', '/');
   result = result.replace('#', '');
   return result;
}

$(document).ready(function($) {

    $(".all-acquired").click(function() {

        var id = encodeURIComponent($(this).attr('topiaId'));

        $.get(createUrl("/season/acquire?id=", id, "&v=true"),
            function(){
                //success
                $(".acquired").prop('checked',true);
            }).fail(function(){
                //fail
                //TODO
            });
    });

    $(".none-acquired").click(function() {
        var id = encodeURIComponent($(this).attr('topiaId'));

        $.get(createUrl("/season/acquire?id=", id, "&v=false"),
            function(){
                //success
                $(".acquired").prop('checked',false);
                $(".seen").prop('checked',false);
            }).fail(function(){
                //fail
                //TODO
            });
    });

    $(".all-seen").click(function() {
        var id = encodeURIComponent($(this).attr('topiaId'));

        $.get(createUrl("/season/watch?id=", id, "&v=true"),
            function(){
                //success
                $(".seen").prop('checked',true);
                $(".acquired").prop('checked', true);
            }).fail(function(){
                //fail
                //TODO
            });
    });

    $(".none-seen").click(function() {
        var id = encodeURIComponent($(this).attr('topiaId'));

        $.get(createUrl("/season/watch?id=", id, "&v=false"),
            function(){
                //success
                $(".seen").prop('checked',false);
            }).fail(function(){
                //fail
                //TODO
            });
    });

//    $(".acquired").click(function() {
//        var checkbox = $(this);
//        var id = encodeURIComponent(checkbox.attr('topiaId'));
//        $.get(createUrl("/acquire?id=", id, "&v=false"),
//            function(){
//                //success
//                checkbox.prop('checked',!checkbox.prop('checked'));
//            }).fail(function(){
//                //fail
//                //TODO
//            });
//    });

});