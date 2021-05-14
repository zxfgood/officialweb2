package com.feeye.bean.FU;

import java.util.List;

/**
 * @author xs
 * @description
 * @date 2019/8/2
 */
public class QunarRefundJson {

    /**
     * code : 0
     * message : SUCCESS
     * createTime : 1564744208713
     * result : {"ret":true,"errmsg":"","data":[{"bizNo":"knc190801131048840e6026","fuwuNo":"SN15190801131317759301","refundStatus":40,"refundReason":20,"refundPrice":1500,"latestRefundTime":"","proveDocumentUrls":[],"passengerList":[{"passengerId":1,"passengerName":"HE/GAOXIN","segments":[{"depAirportCode":"KOS","arrAirportCode":"PVG"}]}]},{"bizNo":"knc190726191817962ec453","fuwuNo":"SN13190728201306121685","refundStatus":40,"refundReason":16,"refundPrice":0,"latestRefundTime":"2019-07-31 20:13:06","proveDocumentUrls":["http://fuwu.qunar.com/gongdan/attach/download?fileName=HO%E5%8C%85%E6%9C%BA%E5%8D%8F%E8%AE%AE%281%29%281%29.docx&uniqueName=file20190729134519995227"],"passengerList":[{"passengerId":1,"passengerName":"YU/BAOHUI","segments":[{"depAirportCode":"KOS","arrAirportCode":"PVG"}]}]},{"bizNo":"knc190729190606309e4125","fuwuNo":"SN03190729194404290727","refundStatus":40,"refundReason":20,"refundPrice":1500,"latestRefundTime":"","proveDocumentUrls":[],"passengerList":[{"passengerId":1,"passengerName":"JI/ZHIMIN","segments":[{"depAirportCode":"KOS","arrAirportCode":"PVG"}]}]},{"bizNo":"knc190802045843976e86c6","fuwuNo":"SN02190802051900818000","refundStatus":40,"refundReason":20,"refundPrice":2720,"latestRefundTime":"","proveDocumentUrls":[],"passengerList":[{"passengerId":1,"passengerName":"ZHANG/FUYUAN","segments":[{"depAirportCode":"KOS","arrAirportCode":"PVG"}]},{"passengerId":2,"passengerName":"ZHANG/HUAJIANG","segments":[{"depAirportCode":"KOS","arrAirportCode":"PVG"}]}]}]}
     */

    private int code;
    private String message;
    private long createTime;
    private ResultBean result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * ret : true
         * errmsg :
         * data : [{"bizNo":"knc190801131048840e6026","fuwuNo":"SN15190801131317759301","refundStatus":40,"refundReason":20,"refundPrice":1500,"latestRefundTime":"","proveDocumentUrls":[],"passengerList":[{"passengerId":1,"passengerName":"HE/GAOXIN","segments":[{"depAirportCode":"KOS","arrAirportCode":"PVG"}]}]},{"bizNo":"knc190726191817962ec453","fuwuNo":"SN13190728201306121685","refundStatus":40,"refundReason":16,"refundPrice":0,"latestRefundTime":"2019-07-31 20:13:06","proveDocumentUrls":["http://fuwu.qunar.com/gongdan/attach/download?fileName=HO%E5%8C%85%E6%9C%BA%E5%8D%8F%E8%AE%AE%281%29%281%29.docx&uniqueName=file20190729134519995227"],"passengerList":[{"passengerId":1,"passengerName":"YU/BAOHUI","segments":[{"depAirportCode":"KOS","arrAirportCode":"PVG"}]}]},{"bizNo":"knc190729190606309e4125","fuwuNo":"SN03190729194404290727","refundStatus":40,"refundReason":20,"refundPrice":1500,"latestRefundTime":"","proveDocumentUrls":[],"passengerList":[{"passengerId":1,"passengerName":"JI/ZHIMIN","segments":[{"depAirportCode":"KOS","arrAirportCode":"PVG"}]}]},{"bizNo":"knc190802045843976e86c6","fuwuNo":"SN02190802051900818000","refundStatus":40,"refundReason":20,"refundPrice":2720,"latestRefundTime":"","proveDocumentUrls":[],"passengerList":[{"passengerId":1,"passengerName":"ZHANG/FUYUAN","segments":[{"depAirportCode":"KOS","arrAirportCode":"PVG"}]},{"passengerId":2,"passengerName":"ZHANG/HUAJIANG","segments":[{"depAirportCode":"KOS","arrAirportCode":"PVG"}]}]}]
         */

        private boolean ret;
        private String errmsg;
        private List <DataBean> data;

        public boolean isRet() {
            return ret;
        }

        public void setRet(boolean ret) {
            this.ret = ret;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public List <DataBean> getData() {
            return data;
        }

        public void setData(List <DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * bizNo : knc190801131048840e6026
             * fuwuNo : SN15190801131317759301
             * refundStatus : 40
             * refundReason : 20
             * refundPrice : 1500
             * latestRefundTime :
             * proveDocumentUrls : []
             * passengerList : [{"passengerId":1,"passengerName":"HE/GAOXIN","segments":[{"depAirportCode":"KOS","arrAirportCode":"PVG"}]}]
             */

            private String bizNo;
            private String fuwuNo;
            private int refundStatus;
            private int refundReason;
            private int refundPrice;
            private String latestRefundTime;
            private List <?> proveDocumentUrls;
            private List <PassengerListBean> passengerList;

            public String getBizNo() {
                return bizNo;
            }

            public void setBizNo(String bizNo) {
                this.bizNo = bizNo;
            }

            public String getFuwuNo() {
                return fuwuNo;
            }

            public void setFuwuNo(String fuwuNo) {
                this.fuwuNo = fuwuNo;
            }

            public int getRefundStatus() {
                return refundStatus;
            }

            public void setRefundStatus(int refundStatus) {
                this.refundStatus = refundStatus;
            }

            public int getRefundReason() {
                return refundReason;
            }

            public void setRefundReason(int refundReason) {
                this.refundReason = refundReason;
            }

            public int getRefundPrice() {
                return refundPrice;
            }

            public void setRefundPrice(int refundPrice) {
                this.refundPrice = refundPrice;
            }

            public String getLatestRefundTime() {
                return latestRefundTime;
            }

            public void setLatestRefundTime(String latestRefundTime) {
                this.latestRefundTime = latestRefundTime;
            }

            public List <?> getProveDocumentUrls() {
                return proveDocumentUrls;
            }

            public void setProveDocumentUrls(List <?> proveDocumentUrls) {
                this.proveDocumentUrls = proveDocumentUrls;
            }

            public List <PassengerListBean> getPassengerList() {
                return passengerList;
            }

            public void setPassengerList(List <PassengerListBean> passengerList) {
                this.passengerList = passengerList;
            }

            public static class PassengerListBean {
                /**
                 * passengerId : 1
                 * passengerName : HE/GAOXIN
                 * segments : [{"depAirportCode":"KOS","arrAirportCode":"PVG"}]
                 */

                private int passengerId;
                private String passengerName;
                private List <SegmentsBean> segments;

                public int getPassengerId() {
                    return passengerId;
                }

                public void setPassengerId(int passengerId) {
                    this.passengerId = passengerId;
                }

                public String getPassengerName() {
                    return passengerName;
                }

                public void setPassengerName(String passengerName) {
                    this.passengerName = passengerName;
                }

                public List <SegmentsBean> getSegments() {
                    return segments;
                }

                public void setSegments(List <SegmentsBean> segments) {
                    this.segments = segments;
                }

                public static class SegmentsBean {
                    /**
                     * depAirportCode : KOS
                     * arrAirportCode : PVG
                     */

                    private String depAirportCode;
                    private String arrAirportCode;

                    public String getDepAirportCode() {
                        return depAirportCode;
                    }

                    public void setDepAirportCode(String depAirportCode) {
                        this.depAirportCode = depAirportCode;
                    }

                    public String getArrAirportCode() {
                        return arrAirportCode;
                    }

                    public void setArrAirportCode(String arrAirportCode) {
                        this.arrAirportCode = arrAirportCode;
                    }
                }
            }
        }
    }
}
