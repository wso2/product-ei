$(window).load(function(){
    var typeAhead = $('.tt-menu'),
        parentWindow = window.parent.document,
        thisParentWrapper = $('#'+gadgets.rpc.RPC_ID, parentWindow).closest('.gadget-body');
    
    $(thisParentWrapper).closest('.ues-component-box').addClass('widget form-control-widget');
    $('body').addClass('widget');
});