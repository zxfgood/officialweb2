<span id="showMsg">Please wait <span style="color: red"
	id="countdown">10</span> seconds for the payment result.<br />支付结果确认中（倒计时请耐心等待<span
	style="color: red" id="countdown1">10</span>秒。)
</span>
<div id='closeWindow' align='center' style='display: none'>
	<a href='#'
		onclick="window.open('', '_self', '');window.opener=null;window.close();"" >Close</a>
</div>
<form id="alipaysubmit" name="alipaysubmit"
	enctype="multipart/form-data"
	action="http://szapgw.shenzhenair.com/szairpay/gateway/gateway-NotifyBusiness-syncNotifyBusiness.action"
	method="post">
	<input type="hidden" name="orderNo" value="NT19030400011223" />
</form>
<script>
	var submit = 10;
	var timer = window.setInterval("countdown();", 1000);
	function countdown() {
		var s = document.getElementById("countdown");
		var s1 = document.getElementById("countdown1");
		if (submit <= 0) {
			document.forms['alipaysubmit'].submit();
			clearInterval(timer);
			var showMsg = document.getElementById("showMsg");
			showMsg.innerHTML = " Jumping merchant pages, please wait a minute！<br/>正在跳转商户页面，请稍后";
			return false;
		}
		var a = submit--;
		s.innerHTML = a;
		s1.innerHTML = a;
	}
</script>