package com.feeye.bean;

import java.util.List;

/**
 * @author xs
 * @date 2019/3/10 23:38
 */
public class CZFlightInfo {
    /**
     * success : true
     * errorCode : 0200
     * errorMsg : 获取旅客航班信息成功
     * data : {"segtype":"S","child":"0","ispreUrlsol":0,"segments":[{"firstflight":{"segInterval":"N","flightNo":"CZ3999","airLine":"中国南方航空公司","codeShare":"","codeShareInfo":"","depPort":"CAN","arrPort":"PEK","depTime":"15:00","arrTime":"18:25","depDate":"20190420","arrDate":"20190420","timeDuringFlight":"3h25m","timeDuringFlightEn":"3Hours 25Minutes","plane":"380","stopNumber":"无","stopNameZh":"无","stopNameEn":"N","meal":"有","term":"T2","rate":"约90%","departureTerminal":"T2","arrivalTerminal":"T2","roundTripCabin":"KA|QA","depAP":"广州","arrAP":"北京","depCity":"广州","arrCity":"北京","depterm":"T2","arrterm":"T2","addday":0,"langcode":"zh","airlineCode":"CZ","planeName":"A380","planeType":"大型","flytype":0,"id":"Shopping:DIRECTCANPEK201904200B2CPC1NEWLAZY","tripIndex":0,"classtype":"odd","count":7,"fgprice":{"F":"9240","J":"6930","W":"2310"},"pprice":{"F":9240,"A":1590,"J":6930,"C":5780,"D":3120,"I":2380,"Y":2310,"B":2170,"M":1940,"H":1820,"U":1710,"L":1480,"E":1250},"pinfo":{"F":"8","A":"&gt;9","J":"&gt;9","C":"&gt;9","D":"&gt;9","I":"2","Y":"&gt;9","B":"&gt;9","M":"&gt;9","H":"&gt;9","U":"&gt;9","L":"&gt;9","E":"&gt;9"},"code":"E","cabinInfo":[{"type":"discount","content":"5.4折"},{"type":"ticket","content":"退改 &yen;375 起","reclass":"blue"},{"type":"mileage","content":"积 954 里程","value":"954##0.0","reclass":"blue"},{"type":"baggage","content":"行李额20KG","value":"20KG","reclass":"zls-baggage","icon":true},{"type":"conpun","content":""}],"price":[1250,1160,230],"typeName":"快乐飞","discount":"54","gbAdultPrice":"1250","fareBasis":["E","YCH","YIN"],"fareReference":["CZB2C-CAN-PEK-PEK-CAN-20APR--S-12901011--","CZB2C-CAN-PEK-PEK-CAN-20APR--S-16CHE03_12901004--","CZB2C-CAN-PEK-PEK-CAN-20APR--S-16INE03_12901004--"],"changeAndRefund":{"change":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;375/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;625/人"}],"refund":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;625/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;1250/人"}],"ch_change":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"免费变更"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;58/人"}],"ch_refund":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;58/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;174/人"}],"in_change":[{"text":"免费变更"}],"in_refund":[{"text":"全部未使用，免退票手续费"}],"sign":"不得签转","isold":false,"ticket":"退改 &yen;375 起","solbool":false,"changeMessageAdZh":"票面价30%, 票面价50%","refundMessageAdZh":"票面价50%, 不得退票","changeMessageChZh":"免费, 票面价5%","refundMessageChZh":"票面价5%, 票面价15%"},"showInfo":"","info":"&gt;9","memcabin":false,"redpur":"","issolbool":false},"segmenttype":0,"sandsflag":0,"fuel":[0,0,0],"tax":50,"redpur":""}],"param":{"it":"0","ct":"0","at":"1","d1":"2019-04-20","c2":"PEK","c1":"CAN-PEK","t":"S","num":1,"n1":"广州-北京","isfirst":true,"leftStatus":false,"rightStatus":false,"isBindCardbycabin":[]},"infant":"0","isMember":false,"adult":"1"}
     * version :
     * server :
     */

    private boolean success;
    private String errorCode;
    private String errorMsg;
    private DataBean data;
    private String version;
    private String server;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public static class DataBean {
        /**
         * segtype : S
         * child : 0
         * ispreUrlsol : 0
         * segments : [{"firstflight":{"segInterval":"N","flightNo":"CZ3999","airLine":"中国南方航空公司","codeShare":"","codeShareInfo":"","depPort":"CAN","arrPort":"PEK","depTime":"15:00","arrTime":"18:25","depDate":"20190420","arrDate":"20190420","timeDuringFlight":"3h25m","timeDuringFlightEn":"3Hours 25Minutes","plane":"380","stopNumber":"无","stopNameZh":"无","stopNameEn":"N","meal":"有","term":"T2","rate":"约90%","departureTerminal":"T2","arrivalTerminal":"T2","roundTripCabin":"KA|QA","depAP":"广州","arrAP":"北京","depCity":"广州","arrCity":"北京","depterm":"T2","arrterm":"T2","addday":0,"langcode":"zh","airlineCode":"CZ","planeName":"A380","planeType":"大型","flytype":0,"id":"Shopping:DIRECTCANPEK201904200B2CPC1NEWLAZY","tripIndex":0,"classtype":"odd","count":7,"fgprice":{"F":"9240","J":"6930","W":"2310"},"pprice":{"F":9240,"A":1590,"J":6930,"C":5780,"D":3120,"I":2380,"Y":2310,"B":2170,"M":1940,"H":1820,"U":1710,"L":1480,"E":1250},"pinfo":{"F":"8","A":"&gt;9","J":"&gt;9","C":"&gt;9","D":"&gt;9","I":"2","Y":"&gt;9","B":"&gt;9","M":"&gt;9","H":"&gt;9","U":"&gt;9","L":"&gt;9","E":"&gt;9"},"code":"E","cabinInfo":[{"type":"discount","content":"5.4折"},{"type":"ticket","content":"退改 &yen;375 起","reclass":"blue"},{"type":"mileage","content":"积 954 里程","value":"954##0.0","reclass":"blue"},{"type":"baggage","content":"行李额20KG","value":"20KG","reclass":"zls-baggage","icon":true},{"type":"conpun","content":""}],"price":[1250,1160,230],"typeName":"快乐飞","discount":"54","gbAdultPrice":"1250","fareBasis":["E","YCH","YIN"],"fareReference":["CZB2C-CAN-PEK-PEK-CAN-20APR--S-12901011--","CZB2C-CAN-PEK-PEK-CAN-20APR--S-16CHE03_12901004--","CZB2C-CAN-PEK-PEK-CAN-20APR--S-16INE03_12901004--"],"changeAndRefund":{"change":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;375/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;625/人"}],"refund":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;625/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;1250/人"}],"ch_change":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"免费变更"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;58/人"}],"ch_refund":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;58/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;174/人"}],"in_change":[{"text":"免费变更"}],"in_refund":[{"text":"全部未使用，免退票手续费"}],"sign":"不得签转","isold":false,"ticket":"退改 &yen;375 起","solbool":false,"changeMessageAdZh":"票面价30%, 票面价50%","refundMessageAdZh":"票面价50%, 不得退票","changeMessageChZh":"免费, 票面价5%","refundMessageChZh":"票面价5%, 票面价15%"},"showInfo":"","info":"&gt;9","memcabin":false,"redpur":"","issolbool":false},"segmenttype":0,"sandsflag":0,"fuel":[0,0,0],"tax":50,"redpur":""}]
         * param : {"it":"0","ct":"0","at":"1","d1":"2019-04-20","c2":"PEK","c1":"CAN-PEK","t":"S","num":1,"n1":"广州-北京","isfirst":true,"leftStatus":false,"rightStatus":false,"isBindCardbycabin":[]}
         * infant : 0
         * isMember : false
         * adult : 1
         */

        private String segtype;
        private String child;
        private int ispreUrlsol;
        private ParamBean param;
        private String infant;
        private boolean isMember;
        private String adult;
        private List <SegmentsBean> segments;

        public String getSegtype() {
            return segtype;
        }

        public void setSegtype(String segtype) {
            this.segtype = segtype;
        }

        public String getChild() {
            return child;
        }

        public void setChild(String child) {
            this.child = child;
        }

        public int getIspreUrlsol() {
            return ispreUrlsol;
        }

        public void setIspreUrlsol(int ispreUrlsol) {
            this.ispreUrlsol = ispreUrlsol;
        }

        public ParamBean getParam() {
            return param;
        }

        public void setParam(ParamBean param) {
            this.param = param;
        }

        public String getInfant() {
            return infant;
        }

        public void setInfant(String infant) {
            this.infant = infant;
        }

        public boolean isIsMember() {
            return isMember;
        }

        public void setIsMember(boolean isMember) {
            this.isMember = isMember;
        }

        public String getAdult() {
            return adult;
        }

        public void setAdult(String adult) {
            this.adult = adult;
        }

        public List <SegmentsBean> getSegments() {
            return segments;
        }

        public void setSegments(List <SegmentsBean> segments) {
            this.segments = segments;
        }

        public static class ParamBean {
            /**
             * it : 0
             * ct : 0
             * at : 1
             * d1 : 2019-04-20
             * c2 : PEK
             * c1 : CAN-PEK
             * t : S
             * num : 1
             * n1 : 广州-北京
             * isfirst : true
             * leftStatus : false
             * rightStatus : false
             * isBindCardbycabin : []
             */

            private String it;
            private String ct;
            private String at;
            private String d1;
            private String c2;
            private String c1;
            private String t;
            private int num;
            private String n1;
            private boolean isfirst;
            private boolean leftStatus;
            private boolean rightStatus;
            private List <?> isBindCardbycabin;

            public String getIt() {
                return it;
            }

            public void setIt(String it) {
                this.it = it;
            }

            public String getCt() {
                return ct;
            }

            public void setCt(String ct) {
                this.ct = ct;
            }

            public String getAt() {
                return at;
            }

            public void setAt(String at) {
                this.at = at;
            }

            public String getD1() {
                return d1;
            }

            public void setD1(String d1) {
                this.d1 = d1;
            }

            public String getC2() {
                return c2;
            }

            public void setC2(String c2) {
                this.c2 = c2;
            }

            public String getC1() {
                return c1;
            }

            public void setC1(String c1) {
                this.c1 = c1;
            }

            public String getT() {
                return t;
            }

            public void setT(String t) {
                this.t = t;
            }

            public int getNum() {
                return num;
            }

            public void setNum(int num) {
                this.num = num;
            }

            public String getN1() {
                return n1;
            }

            public void setN1(String n1) {
                this.n1 = n1;
            }

            public boolean isIsfirst() {
                return isfirst;
            }

            public void setIsfirst(boolean isfirst) {
                this.isfirst = isfirst;
            }

            public boolean isLeftStatus() {
                return leftStatus;
            }

            public void setLeftStatus(boolean leftStatus) {
                this.leftStatus = leftStatus;
            }

            public boolean isRightStatus() {
                return rightStatus;
            }

            public void setRightStatus(boolean rightStatus) {
                this.rightStatus = rightStatus;
            }

            public List <?> getIsBindCardbycabin() {
                return isBindCardbycabin;
            }

            public void setIsBindCardbycabin(List <?> isBindCardbycabin) {
                this.isBindCardbycabin = isBindCardbycabin;
            }
        }

        public static class SegmentsBean {
            /**
             * firstflight : {"segInterval":"N","flightNo":"CZ3999","airLine":"中国南方航空公司","codeShare":"","codeShareInfo":"","depPort":"CAN","arrPort":"PEK","depTime":"15:00","arrTime":"18:25","depDate":"20190420","arrDate":"20190420","timeDuringFlight":"3h25m","timeDuringFlightEn":"3Hours 25Minutes","plane":"380","stopNumber":"无","stopNameZh":"无","stopNameEn":"N","meal":"有","term":"T2","rate":"约90%","departureTerminal":"T2","arrivalTerminal":"T2","roundTripCabin":"KA|QA","depAP":"广州","arrAP":"北京","depCity":"广州","arrCity":"北京","depterm":"T2","arrterm":"T2","addday":0,"langcode":"zh","airlineCode":"CZ","planeName":"A380","planeType":"大型","flytype":0,"id":"Shopping:DIRECTCANPEK201904200B2CPC1NEWLAZY","tripIndex":0,"classtype":"odd","count":7,"fgprice":{"F":"9240","J":"6930","W":"2310"},"pprice":{"F":9240,"A":1590,"J":6930,"C":5780,"D":3120,"I":2380,"Y":2310,"B":2170,"M":1940,"H":1820,"U":1710,"L":1480,"E":1250},"pinfo":{"F":"8","A":"&gt;9","J":"&gt;9","C":"&gt;9","D":"&gt;9","I":"2","Y":"&gt;9","B":"&gt;9","M":"&gt;9","H":"&gt;9","U":"&gt;9","L":"&gt;9","E":"&gt;9"},"code":"E","cabinInfo":[{"type":"discount","content":"5.4折","reclass":"blue","value":"954##0.0","icon":true},{"type":"ticket","content":"退改 &yen;375 起","reclass":"blue"},{"type":"mileage","content":"积 954 里程","value":"954##0.0","reclass":"blue"},{"type":"baggage","content":"行李额20KG","value":"20KG","reclass":"zls-baggage","icon":true},{"type":"conpun","content":""}],"price":[1250,1160,230],"typeName":"快乐飞","discount":"54","gbAdultPrice":"1250","fareBasis":["E","YCH","YIN"],"fareReference":["CZB2C-CAN-PEK-PEK-CAN-20APR--S-12901011--","CZB2C-CAN-PEK-PEK-CAN-20APR--S-16CHE03_12901004--","CZB2C-CAN-PEK-PEK-CAN-20APR--S-16INE03_12901004--"],"changeAndRefund":{"change":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;375/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;625/人"}],"refund":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;625/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;1250/人"}],"ch_change":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"免费变更"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;58/人"}],"ch_refund":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;58/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;174/人"}],"in_change":[{"text":"免费变更"}],"in_refund":[{"text":"全部未使用，免退票手续费"}],"sign":"不得签转","isold":false,"ticket":"退改 &yen;375 起","solbool":false,"changeMessageAdZh":"票面价30%, 票面价50%","refundMessageAdZh":"票面价50%, 不得退票","changeMessageChZh":"免费, 票面价5%","refundMessageChZh":"票面价5%, 票面价15%"},"showInfo":"","info":"&gt;9","memcabin":false,"redpur":"","issolbool":false}
             * segmenttype : 0
             * sandsflag : 0
             * fuel : [0,0,0]
             * tax : 50
             * redpur :
             */

            private FirstflightBean firstflight;
            private int segmenttype;
            private int sandsflag;
            private int tax;
            private String redpur;
            private List <Integer> fuel;

            public FirstflightBean getFirstflight() {
                return firstflight;
            }

            public void setFirstflight(FirstflightBean firstflight) {
                this.firstflight = firstflight;
            }

            public int getSegmenttype() {
                return segmenttype;
            }

            public void setSegmenttype(int segmenttype) {
                this.segmenttype = segmenttype;
            }

            public int getSandsflag() {
                return sandsflag;
            }

            public void setSandsflag(int sandsflag) {
                this.sandsflag = sandsflag;
            }

            public int getTax() {
                return tax;
            }

            public void setTax(int tax) {
                this.tax = tax;
            }

            public String getRedpur() {
                return redpur;
            }

            public void setRedpur(String redpur) {
                this.redpur = redpur;
            }

            public List <Integer> getFuel() {
                return fuel;
            }

            public void setFuel(List <Integer> fuel) {
                this.fuel = fuel;
            }

            public static class FirstflightBean {
                /**
                 * segInterval : N
                 * flightNo : CZ3999
                 * airLine : 中国南方航空公司
                 * codeShare :
                 * codeShareInfo :
                 * depPort : CAN
                 * arrPort : PEK
                 * depTime : 15:00
                 * arrTime : 18:25
                 * depDate : 20190420
                 * arrDate : 20190420
                 * timeDuringFlight : 3h25m
                 * timeDuringFlightEn : 3Hours 25Minutes
                 * plane : 380
                 * stopNumber : 无
                 * stopNameZh : 无
                 * stopNameEn : N
                 * meal : 有
                 * term : T2
                 * rate : 约90%
                 * departureTerminal : T2
                 * arrivalTerminal : T2
                 * roundTripCabin : KA|QA
                 * depAP : 广州
                 * arrAP : 北京
                 * depCity : 广州
                 * arrCity : 北京
                 * depterm : T2
                 * arrterm : T2
                 * addday : 0
                 * langcode : zh
                 * airlineCode : CZ
                 * planeName : A380
                 * planeType : 大型
                 * flytype : 0
                 * id : Shopping:DIRECTCANPEK201904200B2CPC1NEWLAZY
                 * tripIndex : 0
                 * classtype : odd
                 * count : 7
                 * fgprice : {"F":"9240","J":"6930","W":"2310"}
                 * pprice : {"F":9240,"A":1590,"J":6930,"C":5780,"D":3120,"I":2380,"Y":2310,"B":2170,"M":1940,"H":1820,"U":1710,"L":1480,"E":1250}
                 * pinfo : {"F":"8","A":"&gt;9","J":"&gt;9","C":"&gt;9","D":"&gt;9","I":"2","Y":"&gt;9","B":"&gt;9","M":"&gt;9","H":"&gt;9","U":"&gt;9","L":"&gt;9","E":"&gt;9"}
                 * code : E
                 * cabinInfo : [{"type":"discount","content":"5.4折"},{"type":"ticket","content":"退改 &yen;375 起","reclass":"blue"},{"type":"mileage","content":"积 954 里程","value":"954##0.0","reclass":"blue"},{"type":"baggage","content":"行李额20KG","value":"20KG","reclass":"zls-baggage","icon":true},{"type":"conpun","content":""}]
                 * price : [1250,1160,230]
                 * typeName : 快乐飞
                 * discount : 54
                 * gbAdultPrice : 1250
                 * fareBasis : ["E","YCH","YIN"]
                 * fareReference : ["CZB2C-CAN-PEK-PEK-CAN-20APR--S-12901011--","CZB2C-CAN-PEK-PEK-CAN-20APR--S-16CHE03_12901004--","CZB2C-CAN-PEK-PEK-CAN-20APR--S-16INE03_12901004--"]
                 * changeAndRefund : {"change":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;375/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;625/人"}],"refund":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;625/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;1250/人"}],"ch_change":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"免费变更"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;58/人"}],"ch_refund":[{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;58/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;174/人"}],"in_change":[{"text":"免费变更"}],"in_refund":[{"text":"全部未使用，免退票手续费"}],"sign":"不得签转","isold":false,"ticket":"退改 &yen;375 起","solbool":false,"changeMessageAdZh":"票面价30%, 票面价50%","refundMessageAdZh":"票面价50%, 不得退票","changeMessageChZh":"免费, 票面价5%","refundMessageChZh":"票面价5%, 票面价15%"}
                 * showInfo :
                 * info : &gt;9
                 * memcabin : false
                 * redpur :
                 * issolbool : false
                 */

                private String segInterval;
                private String flightNo;
                private String airLine;
                private String codeShare;
                private String codeShareInfo;
                private String depPort;
                private String arrPort;
                private String depTime;
                private String arrTime;
                private String depDate;
                private String arrDate;
                private String timeDuringFlight;
                private String timeDuringFlightEn;
                private String plane;
                private String stopNumber;
                private String stopNameZh;
                private String stopNameEn;
                private String meal;
                private String term;
                private String rate;
                private String departureTerminal;
                private String arrivalTerminal;
                private String roundTripCabin;
                private String depAP;
                private String arrAP;
                private String depCity;
                private String arrCity;
                private String depterm;
                private String arrterm;
                private int addday;
                private String langcode;
                private String airlineCode;
                private String planeName;
                private String planeType;
                private int flytype;
                private String id;
                private int tripIndex;
                private String classtype;
                private int count;
                private FgpriceBean fgprice;
                private PpriceBean pprice;
                private PinfoBean pinfo;
                private String code;
                private String typeName;
                private String discount;
                private String gbAdultPrice;
                private ChangeAndRefundBean changeAndRefund;
                private String showInfo;
                private String info;
                private boolean memcabin;
                private String redpur;
                private boolean issolbool;
                private List <CabinInfoBean> cabinInfo;
                private List <Integer> price;
                private List <String> fareBasis;
                private List <String> fareReference;

                public String getSegInterval() {
                    return segInterval;
                }

                public void setSegInterval(String segInterval) {
                    this.segInterval = segInterval;
                }

                public String getFlightNo() {
                    return flightNo;
                }

                public void setFlightNo(String flightNo) {
                    this.flightNo = flightNo;
                }

                public String getAirLine() {
                    return airLine;
                }

                public void setAirLine(String airLine) {
                    this.airLine = airLine;
                }

                public String getCodeShare() {
                    return codeShare;
                }

                public void setCodeShare(String codeShare) {
                    this.codeShare = codeShare;
                }

                public String getCodeShareInfo() {
                    return codeShareInfo;
                }

                public void setCodeShareInfo(String codeShareInfo) {
                    this.codeShareInfo = codeShareInfo;
                }

                public String getDepPort() {
                    return depPort;
                }

                public void setDepPort(String depPort) {
                    this.depPort = depPort;
                }

                public String getArrPort() {
                    return arrPort;
                }

                public void setArrPort(String arrPort) {
                    this.arrPort = arrPort;
                }

                public String getDepTime() {
                    return depTime;
                }

                public void setDepTime(String depTime) {
                    this.depTime = depTime;
                }

                public String getArrTime() {
                    return arrTime;
                }

                public void setArrTime(String arrTime) {
                    this.arrTime = arrTime;
                }

                public String getDepDate() {
                    return depDate;
                }

                public void setDepDate(String depDate) {
                    this.depDate = depDate;
                }

                public String getArrDate() {
                    return arrDate;
                }

                public void setArrDate(String arrDate) {
                    this.arrDate = arrDate;
                }

                public String getTimeDuringFlight() {
                    return timeDuringFlight;
                }

                public void setTimeDuringFlight(String timeDuringFlight) {
                    this.timeDuringFlight = timeDuringFlight;
                }

                public String getTimeDuringFlightEn() {
                    return timeDuringFlightEn;
                }

                public void setTimeDuringFlightEn(String timeDuringFlightEn) {
                    this.timeDuringFlightEn = timeDuringFlightEn;
                }

                public String getPlane() {
                    return plane;
                }

                public void setPlane(String plane) {
                    this.plane = plane;
                }

                public String getStopNumber() {
                    return stopNumber;
                }

                public void setStopNumber(String stopNumber) {
                    this.stopNumber = stopNumber;
                }

                public String getStopNameZh() {
                    return stopNameZh;
                }

                public void setStopNameZh(String stopNameZh) {
                    this.stopNameZh = stopNameZh;
                }

                public String getStopNameEn() {
                    return stopNameEn;
                }

                public void setStopNameEn(String stopNameEn) {
                    this.stopNameEn = stopNameEn;
                }

                public String getMeal() {
                    return meal;
                }

                public void setMeal(String meal) {
                    this.meal = meal;
                }

                public String getTerm() {
                    return term;
                }

                public void setTerm(String term) {
                    this.term = term;
                }

                public String getRate() {
                    return rate;
                }

                public void setRate(String rate) {
                    this.rate = rate;
                }

                public String getDepartureTerminal() {
                    return departureTerminal;
                }

                public void setDepartureTerminal(String departureTerminal) {
                    this.departureTerminal = departureTerminal;
                }

                public String getArrivalTerminal() {
                    return arrivalTerminal;
                }

                public void setArrivalTerminal(String arrivalTerminal) {
                    this.arrivalTerminal = arrivalTerminal;
                }

                public String getRoundTripCabin() {
                    return roundTripCabin;
                }

                public void setRoundTripCabin(String roundTripCabin) {
                    this.roundTripCabin = roundTripCabin;
                }

                public String getDepAP() {
                    return depAP;
                }

                public void setDepAP(String depAP) {
                    this.depAP = depAP;
                }

                public String getArrAP() {
                    return arrAP;
                }

                public void setArrAP(String arrAP) {
                    this.arrAP = arrAP;
                }

                public String getDepCity() {
                    return depCity;
                }

                public void setDepCity(String depCity) {
                    this.depCity = depCity;
                }

                public String getArrCity() {
                    return arrCity;
                }

                public void setArrCity(String arrCity) {
                    this.arrCity = arrCity;
                }

                public String getDepterm() {
                    return depterm;
                }

                public void setDepterm(String depterm) {
                    this.depterm = depterm;
                }

                public String getArrterm() {
                    return arrterm;
                }

                public void setArrterm(String arrterm) {
                    this.arrterm = arrterm;
                }

                public int getAddday() {
                    return addday;
                }

                public void setAddday(int addday) {
                    this.addday = addday;
                }

                public String getLangcode() {
                    return langcode;
                }

                public void setLangcode(String langcode) {
                    this.langcode = langcode;
                }

                public String getAirlineCode() {
                    return airlineCode;
                }

                public void setAirlineCode(String airlineCode) {
                    this.airlineCode = airlineCode;
                }

                public String getPlaneName() {
                    return planeName;
                }

                public void setPlaneName(String planeName) {
                    this.planeName = planeName;
                }

                public String getPlaneType() {
                    return planeType;
                }

                public void setPlaneType(String planeType) {
                    this.planeType = planeType;
                }

                public int getFlytype() {
                    return flytype;
                }

                public void setFlytype(int flytype) {
                    this.flytype = flytype;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public int getTripIndex() {
                    return tripIndex;
                }

                public void setTripIndex(int tripIndex) {
                    this.tripIndex = tripIndex;
                }

                public String getClasstype() {
                    return classtype;
                }

                public void setClasstype(String classtype) {
                    this.classtype = classtype;
                }

                public int getCount() {
                    return count;
                }

                public void setCount(int count) {
                    this.count = count;
                }

                public FgpriceBean getFgprice() {
                    return fgprice;
                }

                public void setFgprice(FgpriceBean fgprice) {
                    this.fgprice = fgprice;
                }

                public PpriceBean getPprice() {
                    return pprice;
                }

                public void setPprice(PpriceBean pprice) {
                    this.pprice = pprice;
                }

                public PinfoBean getPinfo() {
                    return pinfo;
                }

                public void setPinfo(PinfoBean pinfo) {
                    this.pinfo = pinfo;
                }

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getTypeName() {
                    return typeName;
                }

                public void setTypeName(String typeName) {
                    this.typeName = typeName;
                }

                public String getDiscount() {
                    return discount;
                }

                public void setDiscount(String discount) {
                    this.discount = discount;
                }

                public String getGbAdultPrice() {
                    return gbAdultPrice;
                }

                public void setGbAdultPrice(String gbAdultPrice) {
                    this.gbAdultPrice = gbAdultPrice;
                }

                public ChangeAndRefundBean getChangeAndRefund() {
                    return changeAndRefund;
                }

                public void setChangeAndRefund(ChangeAndRefundBean changeAndRefund) {
                    this.changeAndRefund = changeAndRefund;
                }

                public String getShowInfo() {
                    return showInfo;
                }

                public void setShowInfo(String showInfo) {
                    this.showInfo = showInfo;
                }

                public String getInfo() {
                    return info;
                }

                public void setInfo(String info) {
                    this.info = info;
                }

                public boolean isMemcabin() {
                    return memcabin;
                }

                public void setMemcabin(boolean memcabin) {
                    this.memcabin = memcabin;
                }

                public String getRedpur() {
                    return redpur;
                }

                public void setRedpur(String redpur) {
                    this.redpur = redpur;
                }

                public boolean isIssolbool() {
                    return issolbool;
                }

                public void setIssolbool(boolean issolbool) {
                    this.issolbool = issolbool;
                }

                public List <CabinInfoBean> getCabinInfo() {
                    return cabinInfo;
                }

                public void setCabinInfo(List <CabinInfoBean> cabinInfo) {
                    this.cabinInfo = cabinInfo;
                }

                public List <Integer> getPrice() {
                    return price;
                }

                public void setPrice(List <Integer> price) {
                    this.price = price;
                }

                public List <String> getFareBasis() {
                    return fareBasis;
                }

                public void setFareBasis(List <String> fareBasis) {
                    this.fareBasis = fareBasis;
                }

                public List <String> getFareReference() {
                    return fareReference;
                }

                public void setFareReference(List <String> fareReference) {
                    this.fareReference = fareReference;
                }

                public static class FgpriceBean {
                    /**
                     * F : 9240
                     * J : 6930
                     * W : 2310
                     */

                    private String F;
                    private String J;
                    private String W;

                    public String getF() {
                        return F;
                    }

                    public void setF(String F) {
                        this.F = F;
                    }

                    public String getJ() {
                        return J;
                    }

                    public void setJ(String J) {
                        this.J = J;
                    }

                    public String getW() {
                        return W;
                    }

                    public void setW(String W) {
                        this.W = W;
                    }
                }

                public static class PpriceBean {
                    /**
                     * F : 9240
                     * A : 1590
                     * J : 6930
                     * C : 5780
                     * D : 3120
                     * I : 2380
                     * Y : 2310
                     * B : 2170
                     * M : 1940
                     * H : 1820
                     * U : 1710
                     * L : 1480
                     * E : 1250
                     */

                    private int F;
                    private int A;
                    private int J;
                    private int C;
                    private int D;
                    private int I;
                    private int Y;
                    private int B;
                    private int M;
                    private int H;
                    private int U;
                    private int L;
                    private int E;

                    public int getF() {
                        return F;
                    }

                    public void setF(int F) {
                        this.F = F;
                    }

                    public int getA() {
                        return A;
                    }

                    public void setA(int A) {
                        this.A = A;
                    }

                    public int getJ() {
                        return J;
                    }

                    public void setJ(int J) {
                        this.J = J;
                    }

                    public int getC() {
                        return C;
                    }

                    public void setC(int C) {
                        this.C = C;
                    }

                    public int getD() {
                        return D;
                    }

                    public void setD(int D) {
                        this.D = D;
                    }

                    public int getI() {
                        return I;
                    }

                    public void setI(int I) {
                        this.I = I;
                    }

                    public int getY() {
                        return Y;
                    }

                    public void setY(int Y) {
                        this.Y = Y;
                    }

                    public int getB() {
                        return B;
                    }

                    public void setB(int B) {
                        this.B = B;
                    }

                    public int getM() {
                        return M;
                    }

                    public void setM(int M) {
                        this.M = M;
                    }

                    public int getH() {
                        return H;
                    }

                    public void setH(int H) {
                        this.H = H;
                    }

                    public int getU() {
                        return U;
                    }

                    public void setU(int U) {
                        this.U = U;
                    }

                    public int getL() {
                        return L;
                    }

                    public void setL(int L) {
                        this.L = L;
                    }

                    public int getE() {
                        return E;
                    }

                    public void setE(int E) {
                        this.E = E;
                    }
                }

                public static class PinfoBean {
                    /**
                     * F : 8
                     * A : &gt;9
                     * J : &gt;9
                     * C : &gt;9
                     * D : &gt;9
                     * I : 2
                     * Y : &gt;9
                     * B : &gt;9
                     * M : &gt;9
                     * H : &gt;9
                     * U : &gt;9
                     * L : &gt;9
                     * E : &gt;9
                     */

                    private String F;
                    private String A;
                    private String J;
                    private String C;
                    private String D;
                    private String I;
                    private String Y;
                    private String B;
                    private String M;
                    private String H;
                    private String U;
                    private String L;
                    private String E;

                    public String getF() {
                        return F;
                    }

                    public void setF(String F) {
                        this.F = F;
                    }

                    public String getA() {
                        return A;
                    }

                    public void setA(String A) {
                        this.A = A;
                    }

                    public String getJ() {
                        return J;
                    }

                    public void setJ(String J) {
                        this.J = J;
                    }

                    public String getC() {
                        return C;
                    }

                    public void setC(String C) {
                        this.C = C;
                    }

                    public String getD() {
                        return D;
                    }

                    public void setD(String D) {
                        this.D = D;
                    }

                    public String getI() {
                        return I;
                    }

                    public void setI(String I) {
                        this.I = I;
                    }

                    public String getY() {
                        return Y;
                    }

                    public void setY(String Y) {
                        this.Y = Y;
                    }

                    public String getB() {
                        return B;
                    }

                    public void setB(String B) {
                        this.B = B;
                    }

                    public String getM() {
                        return M;
                    }

                    public void setM(String M) {
                        this.M = M;
                    }

                    public String getH() {
                        return H;
                    }

                    public void setH(String H) {
                        this.H = H;
                    }

                    public String getU() {
                        return U;
                    }

                    public void setU(String U) {
                        this.U = U;
                    }

                    public String getL() {
                        return L;
                    }

                    public void setL(String L) {
                        this.L = L;
                    }

                    public String getE() {
                        return E;
                    }

                    public void setE(String E) {
                        this.E = E;
                    }
                }

                public static class ChangeAndRefundBean {
                    /**
                     * change : [{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;375/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;625/人"}]
                     * refund : [{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;625/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;1250/人"}]
                     * ch_change : [{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"免费变更"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;58/人"}]
                     * ch_refund : [{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":true,"title":"起飞前2小时（含）之前","text":"&yen;58/人"},{"type":false,"val":-2,"isflypre":true,"time":2,"ispre":false,"title":"起飞前2小时之后","text":"&yen;174/人"}]
                     * in_change : [{"text":"免费变更"}]
                     * in_refund : [{"text":"全部未使用，免退票手续费"}]
                     * sign : 不得签转
                     * isold : false
                     * ticket : 退改 &yen;375 起
                     * solbool : false
                     * changeMessageAdZh : 票面价30%, 票面价50%
                     * refundMessageAdZh : 票面价50%, 不得退票
                     * changeMessageChZh : 免费, 票面价5%
                     * refundMessageChZh : 票面价5%, 票面价15%
                     */

                    private String sign;
                    private boolean isold;
                    private String ticket;
                    private boolean solbool;
                    private String changeMessageAdZh;
                    private String refundMessageAdZh;
                    private String changeMessageChZh;
                    private String refundMessageChZh;
                    private List <ChangeBean> change;
                    private List <RefundBean> refund;
                    private List <ChChangeBean> ch_change;
                    private List <ChRefundBean> ch_refund;
                    private List <InChangeBean> in_change;
                    private List <InRefundBean> in_refund;

                    public String getSign() {
                        return sign;
                    }

                    public void setSign(String sign) {
                        this.sign = sign;
                    }

                    public boolean isIsold() {
                        return isold;
                    }

                    public void setIsold(boolean isold) {
                        this.isold = isold;
                    }

                    public String getTicket() {
                        return ticket;
                    }

                    public void setTicket(String ticket) {
                        this.ticket = ticket;
                    }

                    public boolean isSolbool() {
                        return solbool;
                    }

                    public void setSolbool(boolean solbool) {
                        this.solbool = solbool;
                    }

                    public String getChangeMessageAdZh() {
                        return changeMessageAdZh;
                    }

                    public void setChangeMessageAdZh(String changeMessageAdZh) {
                        this.changeMessageAdZh = changeMessageAdZh;
                    }

                    public String getRefundMessageAdZh() {
                        return refundMessageAdZh;
                    }

                    public void setRefundMessageAdZh(String refundMessageAdZh) {
                        this.refundMessageAdZh = refundMessageAdZh;
                    }

                    public String getChangeMessageChZh() {
                        return changeMessageChZh;
                    }

                    public void setChangeMessageChZh(String changeMessageChZh) {
                        this.changeMessageChZh = changeMessageChZh;
                    }

                    public String getRefundMessageChZh() {
                        return refundMessageChZh;
                    }

                    public void setRefundMessageChZh(String refundMessageChZh) {
                        this.refundMessageChZh = refundMessageChZh;
                    }

                    public List <ChangeBean> getChange() {
                        return change;
                    }

                    public void setChange(List <ChangeBean> change) {
                        this.change = change;
                    }

                    public List <RefundBean> getRefund() {
                        return refund;
                    }

                    public void setRefund(List <RefundBean> refund) {
                        this.refund = refund;
                    }

                    public List <ChChangeBean> getCh_change() {
                        return ch_change;
                    }

                    public void setCh_change(List <ChChangeBean> ch_change) {
                        this.ch_change = ch_change;
                    }

                    public List <ChRefundBean> getCh_refund() {
                        return ch_refund;
                    }

                    public void setCh_refund(List <ChRefundBean> ch_refund) {
                        this.ch_refund = ch_refund;
                    }

                    public List <InChangeBean> getIn_change() {
                        return in_change;
                    }

                    public void setIn_change(List <InChangeBean> in_change) {
                        this.in_change = in_change;
                    }

                    public List <InRefundBean> getIn_refund() {
                        return in_refund;
                    }

                    public void setIn_refund(List <InRefundBean> in_refund) {
                        this.in_refund = in_refund;
                    }

                    public static class ChangeBean {
                        /**
                         * type : false
                         * val : -2
                         * isflypre : true
                         * time : 2
                         * ispre : true
                         * title : 起飞前2小时（含）之前
                         * text : &yen;375/人
                         */

                        private boolean type;
                        private int val;
                        private boolean isflypre;
                        private int time;
                        private boolean ispre;
                        private String title;
                        private String text;

                        public boolean isType() {
                            return type;
                        }

                        public void setType(boolean type) {
                            this.type = type;
                        }

                        public int getVal() {
                            return val;
                        }

                        public void setVal(int val) {
                            this.val = val;
                        }

                        public boolean isIsflypre() {
                            return isflypre;
                        }

                        public void setIsflypre(boolean isflypre) {
                            this.isflypre = isflypre;
                        }

                        public int getTime() {
                            return time;
                        }

                        public void setTime(int time) {
                            this.time = time;
                        }

                        public boolean isIspre() {
                            return ispre;
                        }

                        public void setIspre(boolean ispre) {
                            this.ispre = ispre;
                        }

                        public String getTitle() {
                            return title;
                        }

                        public void setTitle(String title) {
                            this.title = title;
                        }

                        public String getText() {
                            return text;
                        }

                        public void setText(String text) {
                            this.text = text;
                        }
                    }

                    public static class RefundBean {
                        /**
                         * type : false
                         * val : -2
                         * isflypre : true
                         * time : 2
                         * ispre : true
                         * title : 起飞前2小时（含）之前
                         * text : &yen;625/人
                         */

                        private boolean type;
                        private int val;
                        private boolean isflypre;
                        private int time;
                        private boolean ispre;
                        private String title;
                        private String text;

                        public boolean isType() {
                            return type;
                        }

                        public void setType(boolean type) {
                            this.type = type;
                        }

                        public int getVal() {
                            return val;
                        }

                        public void setVal(int val) {
                            this.val = val;
                        }

                        public boolean isIsflypre() {
                            return isflypre;
                        }

                        public void setIsflypre(boolean isflypre) {
                            this.isflypre = isflypre;
                        }

                        public int getTime() {
                            return time;
                        }

                        public void setTime(int time) {
                            this.time = time;
                        }

                        public boolean isIspre() {
                            return ispre;
                        }

                        public void setIspre(boolean ispre) {
                            this.ispre = ispre;
                        }

                        public String getTitle() {
                            return title;
                        }

                        public void setTitle(String title) {
                            this.title = title;
                        }

                        public String getText() {
                            return text;
                        }

                        public void setText(String text) {
                            this.text = text;
                        }
                    }

                    public static class ChChangeBean {
                        /**
                         * type : false
                         * val : -2
                         * isflypre : true
                         * time : 2
                         * ispre : true
                         * title : 起飞前2小时（含）之前
                         * text : 免费变更
                         */

                        private boolean type;
                        private int val;
                        private boolean isflypre;
                        private int time;
                        private boolean ispre;
                        private String title;
                        private String text;

                        public boolean isType() {
                            return type;
                        }

                        public void setType(boolean type) {
                            this.type = type;
                        }

                        public int getVal() {
                            return val;
                        }

                        public void setVal(int val) {
                            this.val = val;
                        }

                        public boolean isIsflypre() {
                            return isflypre;
                        }

                        public void setIsflypre(boolean isflypre) {
                            this.isflypre = isflypre;
                        }

                        public int getTime() {
                            return time;
                        }

                        public void setTime(int time) {
                            this.time = time;
                        }

                        public boolean isIspre() {
                            return ispre;
                        }

                        public void setIspre(boolean ispre) {
                            this.ispre = ispre;
                        }

                        public String getTitle() {
                            return title;
                        }

                        public void setTitle(String title) {
                            this.title = title;
                        }

                        public String getText() {
                            return text;
                        }

                        public void setText(String text) {
                            this.text = text;
                        }
                    }

                    public static class ChRefundBean {
                        /**
                         * type : false
                         * val : -2
                         * isflypre : true
                         * time : 2
                         * ispre : true
                         * title : 起飞前2小时（含）之前
                         * text : &yen;58/人
                         */

                        private boolean type;
                        private int val;
                        private boolean isflypre;
                        private int time;
                        private boolean ispre;
                        private String title;
                        private String text;

                        public boolean isType() {
                            return type;
                        }

                        public void setType(boolean type) {
                            this.type = type;
                        }

                        public int getVal() {
                            return val;
                        }

                        public void setVal(int val) {
                            this.val = val;
                        }

                        public boolean isIsflypre() {
                            return isflypre;
                        }

                        public void setIsflypre(boolean isflypre) {
                            this.isflypre = isflypre;
                        }

                        public int getTime() {
                            return time;
                        }

                        public void setTime(int time) {
                            this.time = time;
                        }

                        public boolean isIspre() {
                            return ispre;
                        }

                        public void setIspre(boolean ispre) {
                            this.ispre = ispre;
                        }

                        public String getTitle() {
                            return title;
                        }

                        public void setTitle(String title) {
                            this.title = title;
                        }

                        public String getText() {
                            return text;
                        }

                        public void setText(String text) {
                            this.text = text;
                        }
                    }

                    public static class InChangeBean {
                        /**
                         * text : 免费变更
                         */

                        private String text;

                        public String getText() {
                            return text;
                        }

                        public void setText(String text) {
                            this.text = text;
                        }
                    }

                    public static class InRefundBean {
                        /**
                         * text : 全部未使用，免退票手续费
                         */

                        private String text;

                        public String getText() {
                            return text;
                        }

                        public void setText(String text) {
                            this.text = text;
                        }
                    }
                }

                public static class CabinInfoBean {
                    /**
                     * type : discount
                     * content : 5.4折
                     * reclass : blue
                     * value : 954##0.0
                     * icon : true
                     */

                    private String type;
                    private String content;
                    private String reclass;
                    private String value;
                    private boolean icon;

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public String getContent() {
                        return content;
                    }

                    public void setContent(String content) {
                        this.content = content;
                    }

                    public String getReclass() {
                        return reclass;
                    }

                    public void setReclass(String reclass) {
                        this.reclass = reclass;
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }

                    public boolean isIcon() {
                        return icon;
                    }

                    public void setIcon(boolean icon) {
                        this.icon = icon;
                    }
                }
            }
        }
    }
}
