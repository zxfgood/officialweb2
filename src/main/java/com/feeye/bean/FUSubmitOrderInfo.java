package com.feeye.bean;

import java.util.List;

/**
 * @author xs
 * @date 2019/3/21 17:07
 */
public class FUSubmitOrderInfo {
    private DataBean data;
    private int code;
    private String msg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        private int adultInsuran;
        private Object cartSegmentList;
        private int childInsuran;
        private ContactBean contact;
        private Object ctripLounges;
        private ExtraProductsBean extraProducts;
        private ItineraryBean itinerary;
        private ItineraryParamBean itineraryParam;
        private Object order;
        private Object segmentFares;
        private Object segmentInfos;
        private String version;
        private List <FlightSegFaresBean> flightSegFares;
        private List <FlightsBean> flights;
        private List <InsuranceProductsBean> insuranceProducts;
        private List <PassengersParamsBean> passengersParams;

        public int getAdultInsuran() {
            return adultInsuran;
        }

        public void setAdultInsuran(int adultInsuran) {
            this.adultInsuran = adultInsuran;
        }

        public Object getCartSegmentList() {
            return cartSegmentList;
        }

        public void setCartSegmentList(Object cartSegmentList) {
            this.cartSegmentList = cartSegmentList;
        }

        public int getChildInsuran() {
            return childInsuran;
        }

        public void setChildInsuran(int childInsuran) {
            this.childInsuran = childInsuran;
        }

        public ContactBean getContact() {
            return contact;
        }

        public void setContact(ContactBean contact) {
            this.contact = contact;
        }

        public Object getCtripLounges() {
            return ctripLounges;
        }

        public void setCtripLounges(Object ctripLounges) {
            this.ctripLounges = ctripLounges;
        }

        public ExtraProductsBean getExtraProducts() {
            return extraProducts;
        }

        public void setExtraProducts(ExtraProductsBean extraProducts) {
            this.extraProducts = extraProducts;
        }

        public ItineraryBean getItinerary() {
            return itinerary;
        }

        public void setItinerary(ItineraryBean itinerary) {
            this.itinerary = itinerary;
        }

        public ItineraryParamBean getItineraryParam() {
            return itineraryParam;
        }

        public void setItineraryParam(ItineraryParamBean itineraryParam) {
            this.itineraryParam = itineraryParam;
        }

        public Object getOrder() {
            return order;
        }

        public void setOrder(Object order) {
            this.order = order;
        }

        public Object getSegmentFares() {
            return segmentFares;
        }

        public void setSegmentFares(Object segmentFares) {
            this.segmentFares = segmentFares;
        }

        public Object getSegmentInfos() {
            return segmentInfos;
        }

        public void setSegmentInfos(Object segmentInfos) {
            this.segmentInfos = segmentInfos;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List <FlightSegFaresBean> getFlightSegFares() {
            return flightSegFares;
        }

        public void setFlightSegFares(List <FlightSegFaresBean> flightSegFares) {
            this.flightSegFares = flightSegFares;
        }

        public List <FlightsBean> getFlights() {
            return flights;
        }

        public void setFlights(List <FlightsBean> flights) {
            this.flights = flights;
        }

        public List <InsuranceProductsBean> getInsuranceProducts() {
            return insuranceProducts;
        }

        public void setInsuranceProducts(List <InsuranceProductsBean> insuranceProducts) {
            this.insuranceProducts = insuranceProducts;
        }

        public List <PassengersParamsBean> getPassengersParams() {
            return passengersParams;
        }

        public void setPassengersParams(List <PassengersParamsBean> passengersParams) {
            this.passengersParams = passengersParams;
        }

        public static class ContactBean {
            /**
             * contactAddress :
             * contactEmail :
             * contactMobile : 13682690632
             * contactName : ?????????
             */

            private String contactAddress;
            private String contactEmail;
            private String contactMobile;
            private String contactName;

            public String getContactAddress() {
                return contactAddress;
            }

            public void setContactAddress(String contactAddress) {
                this.contactAddress = contactAddress;
            }

            public String getContactEmail() {
                return contactEmail;
            }

            public void setContactEmail(String contactEmail) {
                this.contactEmail = contactEmail;
            }

            public String getContactMobile() {
                return contactMobile;
            }

            public void setContactMobile(String contactMobile) {
                this.contactMobile = contactMobile;
            }

            public String getContactName() {
                return contactName;
            }

            public void setContactName(String contactName) {
                this.contactName = contactName;
            }
        }

        public static class ExtraProductsBean {
            public static class TOBean {
                public static class ProductBean {
                }
            }
        }

        public static class ItineraryBean {
        }

        public static class ItineraryParamBean {
            /**
             * address : null
             * billflag : false
             * canton : null
             * city : null
             * fare : null
             * name : null
             * offerItemId : null
             * postcode : null
             * province : null
             * realAddress : null
             * tel : null
             */

            private Object address;
            private boolean billflag;
            private Object canton;
            private Object city;
            private Object fare;
            private Object name;
            private Object offerItemId;
            private Object postcode;
            private Object province;
            private Object realAddress;
            private Object tel;

            public Object getAddress() {
                return address;
            }

            public void setAddress(Object address) {
                this.address = address;
            }

            public boolean isBillflag() {
                return billflag;
            }

            public void setBillflag(boolean billflag) {
                this.billflag = billflag;
            }

            public Object getCanton() {
                return canton;
            }

            public void setCanton(Object canton) {
                this.canton = canton;
            }

            public Object getCity() {
                return city;
            }

            public void setCity(Object city) {
                this.city = city;
            }

            public Object getFare() {
                return fare;
            }

            public void setFare(Object fare) {
                this.fare = fare;
            }

            public Object getName() {
                return name;
            }

            public void setName(Object name) {
                this.name = name;
            }

            public Object getOfferItemId() {
                return offerItemId;
            }

            public void setOfferItemId(Object offerItemId) {
                this.offerItemId = offerItemId;
            }

            public Object getPostcode() {
                return postcode;
            }

            public void setPostcode(Object postcode) {
                this.postcode = postcode;
            }

            public Object getProvince() {
                return province;
            }

            public void setProvince(Object province) {
                this.province = province;
            }

            public Object getRealAddress() {
                return realAddress;
            }

            public void setRealAddress(Object realAddress) {
                this.realAddress = realAddress;
            }

            public Object getTel() {
                return tel;
            }

            public void setTel(Object tel) {
                this.tel = tel;
            }
        }

        public static class FlightSegFaresBean {
            private List <SegmentFaresBean> segmentFares;

            public List <SegmentFaresBean> getSegmentFares() {
                return segmentFares;
            }

            public void setSegmentFares(List <SegmentFaresBean> segmentFares) {
                this.segmentFares = segmentFares;
            }

            public static class SegmentFaresBean {
                /**
                 * fareDetails : [{"airportTax":50,"commission":{"agentFee":0,"agentPer":0,"policyCreateDate":null,"policyNo":null,"spFee":0,"spPer":0},"farePrice":444,"fuelSurcharge":0,"marketFare":450,"netFare":444,"otherCharges":0,"passengerCount":1,"passengerType":"ADULT","pnCMBCardMile":0,"policyNo":null,"refrenceCabin":null,"refrenceFare":0,"remark":null,"returnPoint":0,"total":494}]
                 * gwcInfo : []
                 * intlFareDetails : []
                 * product : {"adultFare":{"airportTax":50,"commission":{"agentFee":0,"agentPer":0,"policyCreateDate":null,"policyNo":null,"spFee":0,"spPer":0},"farePrice":444,"fuelSurcharge":0,"marketFare":450,"otherCharges":0,"pnCMBCardMile":0,"policyNo":null,"refrenceCabin":null,"refrenceFare":0,"remark":null,"returnPoint":0,"total":494},"adultFareIntl":null,"airlineCode":null,"channel":null,"channelHidden":null,"childFare":{"airportTax":0,"commission":{"agentFee":0,"agentPer":0,"policyCreateDate":null,"policyNo":null,"spFee":0,"spPer":0},"farePrice":444,"fuelSurcharge":0,"marketFare":450,"otherCharges":0,"pnCMBCardMile":0,"policyNo":null,"refrenceCabin":null,"refrenceFare":0,"remark":null,"returnPoint":0,"total":444},"childFareIntl":null,"childMinus":0,"code":"WONLY","createDate":null,"createId":null,"description":null,"ei":null,"fareLocks":[],"id":null,"infantFare":{"airportTax":0,"commission":{"agentFee":0,"agentPer":0,"policyCreateDate":null,"policyNo":null,"spFee":0,"spPer":0},"farePrice":260,"fuelSurcharge":0,"marketFare":260,"otherCharges":0,"pnCMBCardMile":0,"policyNo":null,"refrenceCabin":null,"refrenceFare":0,"remark":null,"returnPoint":0,"total":260},"infantFareIntl":null,"isAdvance":null,"minuseFare":0,"name":"????????????","offerItemId":"OW-TICKET-7fb36bbd110641399e590c927d19352e#OW-FOC-HRB-20190531-FU6638-WONLY-R","recommended":false,"remark":"1???????????????????????????????????????<br/> 2???????????????????????????????????????168??????????????????????????????????????????????????????0%?????????????????????????????????48??????????????????168???????????????????????????????????????????????????0%?????????????????????????????????4??????????????????48???????????????????????????????????????????????????5%?????????????????????????????????4?????????????????????????????????????????????????????????10%???????????? <br/>3???????????????????????????????????????168?????????????????????????????????????????????5%?????????????????????????????????48??????????????????168??????????????????????????????????????????5%?????????????????????????????????4??????????????????48??????????????????????????????????????????5%?????????????????????????????????4??????????????????????????????????????????????????????10%????????????","seq":null,"status":null,"userTypeMark":""}
                 * segment : {"airlineCode":"FU","arrCode":"HRB","arrCodeName":null,"arrTime":"2019-05-31T23:00:00","arriTerm":"T2","baseFare":2580,"cabinClass":"R","cabinPrice":444,"cabinState":"OP","checkInUserId":null,"depCode":"FOC","depCodeName":null,"depTerm":"--","depTime":"2019-05-31T17:30:00","depTimeforTQ":"2019-05-31T00:00:00","flightNo":"FU6638","id":null,"inventory":"IBE","lowstCabin":null,"luggageProductList":[],"mealProductList":[],"offerItemId":"OW-TICKET-7fb36bbd110641399e590c927d19352e#OW-FOC-HRB-20190531-FU6638-WONLY-R","orderId":null,"planeType":"738","point":null,"referenceCabin":"","referenceFare":0,"stopArrTime":null,"stopCity":"??????","stopDepTime":null,"timeLimit":null,"tripType":"ONEWAY","yktProductList":[]}
                 */

                private ProductBeanX product;
                private SegmentBean segment;
                private List <FareDetailsBean> fareDetails;
                private List <?> gwcInfo;
                private List <?> intlFareDetails;

                public ProductBeanX getProduct() {
                    return product;
                }

                public void setProduct(ProductBeanX product) {
                    this.product = product;
                }

                public SegmentBean getSegment() {
                    return segment;
                }

                public void setSegment(SegmentBean segment) {
                    this.segment = segment;
                }

                public List <FareDetailsBean> getFareDetails() {
                    return fareDetails;
                }

                public void setFareDetails(List <FareDetailsBean> fareDetails) {
                    this.fareDetails = fareDetails;
                }

                public List <?> getGwcInfo() {
                    return gwcInfo;
                }

                public void setGwcInfo(List <?> gwcInfo) {
                    this.gwcInfo = gwcInfo;
                }

                public List <?> getIntlFareDetails() {
                    return intlFareDetails;
                }

                public void setIntlFareDetails(List <?> intlFareDetails) {
                    this.intlFareDetails = intlFareDetails;
                }

                public static class ProductBeanX {
                    /**
                     * adultFare : {"airportTax":50,"commission":{"agentFee":0,"agentPer":0,"policyCreateDate":null,"policyNo":null,"spFee":0,"spPer":0},"farePrice":444,"fuelSurcharge":0,"marketFare":450,"otherCharges":0,"pnCMBCardMile":0,"policyNo":null,"refrenceCabin":null,"refrenceFare":0,"remark":null,"returnPoint":0,"total":494}
                     * adultFareIntl : null
                     * airlineCode : null
                     * channel : null
                     * channelHidden : null
                     * childFare : {"airportTax":0,"commission":{"agentFee":0,"agentPer":0,"policyCreateDate":null,"policyNo":null,"spFee":0,"spPer":0},"farePrice":444,"fuelSurcharge":0,"marketFare":450,"otherCharges":0,"pnCMBCardMile":0,"policyNo":null,"refrenceCabin":null,"refrenceFare":0,"remark":null,"returnPoint":0,"total":444}
                     * childFareIntl : null
                     * childMinus : 0
                     * code : WONLY
                     * createDate : null
                     * createId : null
                     * description : null
                     * ei : null
                     * fareLocks : []
                     * id : null
                     * infantFare : {"airportTax":0,"commission":{"agentFee":0,"agentPer":0,"policyCreateDate":null,"policyNo":null,"spFee":0,"spPer":0},"farePrice":260,"fuelSurcharge":0,"marketFare":260,"otherCharges":0,"pnCMBCardMile":0,"policyNo":null,"refrenceCabin":null,"refrenceFare":0,"remark":null,"returnPoint":0,"total":260}
                     * infantFareIntl : null
                     * isAdvance : null
                     * minuseFare : 0.0
                     * name : ????????????
                     * offerItemId : OW-TICKET-7fb36bbd110641399e590c927d19352e#OW-FOC-HRB-20190531-FU6638-WONLY-R
                     * recommended : false
                     * remark : 1???????????????????????????????????????<br/> 2???????????????????????????????????????168??????????????????????????????????????????????????????0%?????????????????????????????????48??????????????????168???????????????????????????????????????????????????0%?????????????????????????????????4??????????????????48???????????????????????????????????????????????????5%?????????????????????????????????4?????????????????????????????????????????????????????????10%???????????? <br/>3???????????????????????????????????????168?????????????????????????????????????????????5%?????????????????????????????????48??????????????????168??????????????????????????????????????????5%?????????????????????????????????4??????????????????48??????????????????????????????????????????5%?????????????????????????????????4??????????????????????????????????????????????????????10%????????????
                     * seq : null
                     * status : null
                     * userTypeMark :
                     */

                    private AdultFareBean adultFare;
                    private Object adultFareIntl;
                    private Object airlineCode;
                    private Object channel;
                    private Object channelHidden;
                    private ChildFareBean childFare;
                    private Object childFareIntl;
                    private int childMinus;
                    private String code;
                    private Object createDate;
                    private Object createId;
                    private Object description;
                    private Object ei;
                    private Object id;
                    private InfantFareBean infantFare;
                    private Object infantFareIntl;
                    private Object isAdvance;
                    private double minuseFare;
                    private String name;
                    private String offerItemId;
                    private boolean recommended;
                    private String remark;
                    private Object seq;
                    private Object status;
                    private String userTypeMark;
                    private List <?> fareLocks;

                    public AdultFareBean getAdultFare() {
                        return adultFare;
                    }

                    public void setAdultFare(AdultFareBean adultFare) {
                        this.adultFare = adultFare;
                    }

                    public Object getAdultFareIntl() {
                        return adultFareIntl;
                    }

                    public void setAdultFareIntl(Object adultFareIntl) {
                        this.adultFareIntl = adultFareIntl;
                    }

                    public Object getAirlineCode() {
                        return airlineCode;
                    }

                    public void setAirlineCode(Object airlineCode) {
                        this.airlineCode = airlineCode;
                    }

                    public Object getChannel() {
                        return channel;
                    }

                    public void setChannel(Object channel) {
                        this.channel = channel;
                    }

                    public Object getChannelHidden() {
                        return channelHidden;
                    }

                    public void setChannelHidden(Object channelHidden) {
                        this.channelHidden = channelHidden;
                    }

                    public ChildFareBean getChildFare() {
                        return childFare;
                    }

                    public void setChildFare(ChildFareBean childFare) {
                        this.childFare = childFare;
                    }

                    public Object getChildFareIntl() {
                        return childFareIntl;
                    }

                    public void setChildFareIntl(Object childFareIntl) {
                        this.childFareIntl = childFareIntl;
                    }

                    public int getChildMinus() {
                        return childMinus;
                    }

                    public void setChildMinus(int childMinus) {
                        this.childMinus = childMinus;
                    }

                    public String getCode() {
                        return code;
                    }

                    public void setCode(String code) {
                        this.code = code;
                    }

                    public Object getCreateDate() {
                        return createDate;
                    }

                    public void setCreateDate(Object createDate) {
                        this.createDate = createDate;
                    }

                    public Object getCreateId() {
                        return createId;
                    }

                    public void setCreateId(Object createId) {
                        this.createId = createId;
                    }

                    public Object getDescription() {
                        return description;
                    }

                    public void setDescription(Object description) {
                        this.description = description;
                    }

                    public Object getEi() {
                        return ei;
                    }

                    public void setEi(Object ei) {
                        this.ei = ei;
                    }

                    public Object getId() {
                        return id;
                    }

                    public void setId(Object id) {
                        this.id = id;
                    }

                    public InfantFareBean getInfantFare() {
                        return infantFare;
                    }

                    public void setInfantFare(InfantFareBean infantFare) {
                        this.infantFare = infantFare;
                    }

                    public Object getInfantFareIntl() {
                        return infantFareIntl;
                    }

                    public void setInfantFareIntl(Object infantFareIntl) {
                        this.infantFareIntl = infantFareIntl;
                    }

                    public Object getIsAdvance() {
                        return isAdvance;
                    }

                    public void setIsAdvance(Object isAdvance) {
                        this.isAdvance = isAdvance;
                    }

                    public double getMinuseFare() {
                        return minuseFare;
                    }

                    public void setMinuseFare(double minuseFare) {
                        this.minuseFare = minuseFare;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getOfferItemId() {
                        return offerItemId;
                    }

                    public void setOfferItemId(String offerItemId) {
                        this.offerItemId = offerItemId;
                    }

                    public boolean isRecommended() {
                        return recommended;
                    }

                    public void setRecommended(boolean recommended) {
                        this.recommended = recommended;
                    }

                    public String getRemark() {
                        return remark;
                    }

                    public void setRemark(String remark) {
                        this.remark = remark;
                    }

                    public Object getSeq() {
                        return seq;
                    }

                    public void setSeq(Object seq) {
                        this.seq = seq;
                    }

                    public Object getStatus() {
                        return status;
                    }

                    public void setStatus(Object status) {
                        this.status = status;
                    }

                    public String getUserTypeMark() {
                        return userTypeMark;
                    }

                    public void setUserTypeMark(String userTypeMark) {
                        this.userTypeMark = userTypeMark;
                    }

                    public List <?> getFareLocks() {
                        return fareLocks;
                    }

                    public void setFareLocks(List <?> fareLocks) {
                        this.fareLocks = fareLocks;
                    }

                    public static class AdultFareBean {
                        /**
                         * airportTax : 50.0
                         * commission : {"agentFee":0,"agentPer":0,"policyCreateDate":null,"policyNo":null,"spFee":0,"spPer":0}
                         * farePrice : 444.0
                         * fuelSurcharge : 0.0
                         * marketFare : 450.0
                         * otherCharges : 0.0
                         * pnCMBCardMile : 0.0
                         * policyNo : null
                         * refrenceCabin : null
                         * refrenceFare : 0.0
                         * remark : null
                         * returnPoint : 0.0
                         * total : 494.0
                         */

                        private double airportTax;
                        private CommissionBean commission;
                        private double farePrice;
                        private double fuelSurcharge;
                        private double marketFare;
                        private double otherCharges;
                        private double pnCMBCardMile;
                        private Object policyNo;
                        private Object refrenceCabin;
                        private double refrenceFare;
                        private Object remark;
                        private double returnPoint;
                        private double total;

                        public double getAirportTax() {
                            return airportTax;
                        }

                        public void setAirportTax(double airportTax) {
                            this.airportTax = airportTax;
                        }

                        public CommissionBean getCommission() {
                            return commission;
                        }

                        public void setCommission(CommissionBean commission) {
                            this.commission = commission;
                        }

                        public double getFarePrice() {
                            return farePrice;
                        }

                        public void setFarePrice(double farePrice) {
                            this.farePrice = farePrice;
                        }

                        public double getFuelSurcharge() {
                            return fuelSurcharge;
                        }

                        public void setFuelSurcharge(double fuelSurcharge) {
                            this.fuelSurcharge = fuelSurcharge;
                        }

                        public double getMarketFare() {
                            return marketFare;
                        }

                        public void setMarketFare(double marketFare) {
                            this.marketFare = marketFare;
                        }

                        public double getOtherCharges() {
                            return otherCharges;
                        }

                        public void setOtherCharges(double otherCharges) {
                            this.otherCharges = otherCharges;
                        }

                        public double getPnCMBCardMile() {
                            return pnCMBCardMile;
                        }

                        public void setPnCMBCardMile(double pnCMBCardMile) {
                            this.pnCMBCardMile = pnCMBCardMile;
                        }

                        public Object getPolicyNo() {
                            return policyNo;
                        }

                        public void setPolicyNo(Object policyNo) {
                            this.policyNo = policyNo;
                        }

                        public Object getRefrenceCabin() {
                            return refrenceCabin;
                        }

                        public void setRefrenceCabin(Object refrenceCabin) {
                            this.refrenceCabin = refrenceCabin;
                        }

                        public double getRefrenceFare() {
                            return refrenceFare;
                        }

                        public void setRefrenceFare(double refrenceFare) {
                            this.refrenceFare = refrenceFare;
                        }

                        public Object getRemark() {
                            return remark;
                        }

                        public void setRemark(Object remark) {
                            this.remark = remark;
                        }

                        public double getReturnPoint() {
                            return returnPoint;
                        }

                        public void setReturnPoint(double returnPoint) {
                            this.returnPoint = returnPoint;
                        }

                        public double getTotal() {
                            return total;
                        }

                        public void setTotal(double total) {
                            this.total = total;
                        }

                        public static class CommissionBean {
                            /**
                             * agentFee : 0.0
                             * agentPer : 0.0
                             * policyCreateDate : null
                             * policyNo : null
                             * spFee : 0.0
                             * spPer : 0.0
                             */

                            private double agentFee;
                            private double agentPer;
                            private Object policyCreateDate;
                            private Object policyNo;
                            private double spFee;
                            private double spPer;

                            public double getAgentFee() {
                                return agentFee;
                            }

                            public void setAgentFee(double agentFee) {
                                this.agentFee = agentFee;
                            }

                            public double getAgentPer() {
                                return agentPer;
                            }

                            public void setAgentPer(double agentPer) {
                                this.agentPer = agentPer;
                            }

                            public Object getPolicyCreateDate() {
                                return policyCreateDate;
                            }

                            public void setPolicyCreateDate(Object policyCreateDate) {
                                this.policyCreateDate = policyCreateDate;
                            }

                            public Object getPolicyNo() {
                                return policyNo;
                            }

                            public void setPolicyNo(Object policyNo) {
                                this.policyNo = policyNo;
                            }

                            public double getSpFee() {
                                return spFee;
                            }

                            public void setSpFee(double spFee) {
                                this.spFee = spFee;
                            }

                            public double getSpPer() {
                                return spPer;
                            }

                            public void setSpPer(double spPer) {
                                this.spPer = spPer;
                            }
                        }
                    }

                    public static class ChildFareBean {
                        /**
                         * airportTax : 0.0
                         * commission : {"agentFee":0,"agentPer":0,"policyCreateDate":null,"policyNo":null,"spFee":0,"spPer":0}
                         * farePrice : 444.0
                         * fuelSurcharge : 0.0
                         * marketFare : 450.0
                         * otherCharges : 0.0
                         * pnCMBCardMile : 0.0
                         * policyNo : null
                         * refrenceCabin : null
                         * refrenceFare : 0.0
                         * remark : null
                         * returnPoint : 0.0
                         * total : 444.0
                         */

                        private double airportTax;
                        private CommissionBeanX commission;
                        private double farePrice;
                        private double fuelSurcharge;
                        private double marketFare;
                        private double otherCharges;
                        private double pnCMBCardMile;
                        private Object policyNo;
                        private Object refrenceCabin;
                        private double refrenceFare;
                        private Object remark;
                        private double returnPoint;
                        private double total;

                        public double getAirportTax() {
                            return airportTax;
                        }

                        public void setAirportTax(double airportTax) {
                            this.airportTax = airportTax;
                        }

                        public CommissionBeanX getCommission() {
                            return commission;
                        }

                        public void setCommission(CommissionBeanX commission) {
                            this.commission = commission;
                        }

                        public double getFarePrice() {
                            return farePrice;
                        }

                        public void setFarePrice(double farePrice) {
                            this.farePrice = farePrice;
                        }

                        public double getFuelSurcharge() {
                            return fuelSurcharge;
                        }

                        public void setFuelSurcharge(double fuelSurcharge) {
                            this.fuelSurcharge = fuelSurcharge;
                        }

                        public double getMarketFare() {
                            return marketFare;
                        }

                        public void setMarketFare(double marketFare) {
                            this.marketFare = marketFare;
                        }

                        public double getOtherCharges() {
                            return otherCharges;
                        }

                        public void setOtherCharges(double otherCharges) {
                            this.otherCharges = otherCharges;
                        }

                        public double getPnCMBCardMile() {
                            return pnCMBCardMile;
                        }

                        public void setPnCMBCardMile(double pnCMBCardMile) {
                            this.pnCMBCardMile = pnCMBCardMile;
                        }

                        public Object getPolicyNo() {
                            return policyNo;
                        }

                        public void setPolicyNo(Object policyNo) {
                            this.policyNo = policyNo;
                        }

                        public Object getRefrenceCabin() {
                            return refrenceCabin;
                        }

                        public void setRefrenceCabin(Object refrenceCabin) {
                            this.refrenceCabin = refrenceCabin;
                        }

                        public double getRefrenceFare() {
                            return refrenceFare;
                        }

                        public void setRefrenceFare(double refrenceFare) {
                            this.refrenceFare = refrenceFare;
                        }

                        public Object getRemark() {
                            return remark;
                        }

                        public void setRemark(Object remark) {
                            this.remark = remark;
                        }

                        public double getReturnPoint() {
                            return returnPoint;
                        }

                        public void setReturnPoint(double returnPoint) {
                            this.returnPoint = returnPoint;
                        }

                        public double getTotal() {
                            return total;
                        }

                        public void setTotal(double total) {
                            this.total = total;
                        }

                        public static class CommissionBeanX {
                            /**
                             * agentFee : 0.0
                             * agentPer : 0.0
                             * policyCreateDate : null
                             * policyNo : null
                             * spFee : 0.0
                             * spPer : 0.0
                             */

                            private double agentFee;
                            private double agentPer;
                            private Object policyCreateDate;
                            private Object policyNo;
                            private double spFee;
                            private double spPer;

                            public double getAgentFee() {
                                return agentFee;
                            }

                            public void setAgentFee(double agentFee) {
                                this.agentFee = agentFee;
                            }

                            public double getAgentPer() {
                                return agentPer;
                            }

                            public void setAgentPer(double agentPer) {
                                this.agentPer = agentPer;
                            }

                            public Object getPolicyCreateDate() {
                                return policyCreateDate;
                            }

                            public void setPolicyCreateDate(Object policyCreateDate) {
                                this.policyCreateDate = policyCreateDate;
                            }

                            public Object getPolicyNo() {
                                return policyNo;
                            }

                            public void setPolicyNo(Object policyNo) {
                                this.policyNo = policyNo;
                            }

                            public double getSpFee() {
                                return spFee;
                            }

                            public void setSpFee(double spFee) {
                                this.spFee = spFee;
                            }

                            public double getSpPer() {
                                return spPer;
                            }

                            public void setSpPer(double spPer) {
                                this.spPer = spPer;
                            }
                        }
                    }

                    public static class InfantFareBean {
                        /**
                         * airportTax : 0.0
                         * commission : {"agentFee":0,"agentPer":0,"policyCreateDate":null,"policyNo":null,"spFee":0,"spPer":0}
                         * farePrice : 260.0
                         * fuelSurcharge : 0.0
                         * marketFare : 260.0
                         * otherCharges : 0.0
                         * pnCMBCardMile : 0.0
                         * policyNo : null
                         * refrenceCabin : null
                         * refrenceFare : 0.0
                         * remark : null
                         * returnPoint : 0.0
                         * total : 260.0
                         */

                        private double airportTax;
                        private CommissionBeanXX commission;
                        private double farePrice;
                        private double fuelSurcharge;
                        private double marketFare;
                        private double otherCharges;
                        private double pnCMBCardMile;
                        private Object policyNo;
                        private Object refrenceCabin;
                        private double refrenceFare;
                        private Object remark;
                        private double returnPoint;
                        private double total;

                        public double getAirportTax() {
                            return airportTax;
                        }

                        public void setAirportTax(double airportTax) {
                            this.airportTax = airportTax;
                        }

                        public CommissionBeanXX getCommission() {
                            return commission;
                        }

                        public void setCommission(CommissionBeanXX commission) {
                            this.commission = commission;
                        }

                        public double getFarePrice() {
                            return farePrice;
                        }

                        public void setFarePrice(double farePrice) {
                            this.farePrice = farePrice;
                        }

                        public double getFuelSurcharge() {
                            return fuelSurcharge;
                        }

                        public void setFuelSurcharge(double fuelSurcharge) {
                            this.fuelSurcharge = fuelSurcharge;
                        }

                        public double getMarketFare() {
                            return marketFare;
                        }

                        public void setMarketFare(double marketFare) {
                            this.marketFare = marketFare;
                        }

                        public double getOtherCharges() {
                            return otherCharges;
                        }

                        public void setOtherCharges(double otherCharges) {
                            this.otherCharges = otherCharges;
                        }

                        public double getPnCMBCardMile() {
                            return pnCMBCardMile;
                        }

                        public void setPnCMBCardMile(double pnCMBCardMile) {
                            this.pnCMBCardMile = pnCMBCardMile;
                        }

                        public Object getPolicyNo() {
                            return policyNo;
                        }

                        public void setPolicyNo(Object policyNo) {
                            this.policyNo = policyNo;
                        }

                        public Object getRefrenceCabin() {
                            return refrenceCabin;
                        }

                        public void setRefrenceCabin(Object refrenceCabin) {
                            this.refrenceCabin = refrenceCabin;
                        }

                        public double getRefrenceFare() {
                            return refrenceFare;
                        }

                        public void setRefrenceFare(double refrenceFare) {
                            this.refrenceFare = refrenceFare;
                        }

                        public Object getRemark() {
                            return remark;
                        }

                        public void setRemark(Object remark) {
                            this.remark = remark;
                        }

                        public double getReturnPoint() {
                            return returnPoint;
                        }

                        public void setReturnPoint(double returnPoint) {
                            this.returnPoint = returnPoint;
                        }

                        public double getTotal() {
                            return total;
                        }

                        public void setTotal(double total) {
                            this.total = total;
                        }

                        public static class CommissionBeanXX {
                            /**
                             * agentFee : 0.0
                             * agentPer : 0.0
                             * policyCreateDate : null
                             * policyNo : null
                             * spFee : 0.0
                             * spPer : 0.0
                             */

                            private double agentFee;
                            private double agentPer;
                            private Object policyCreateDate;
                            private Object policyNo;
                            private double spFee;
                            private double spPer;

                            public double getAgentFee() {
                                return agentFee;
                            }

                            public void setAgentFee(double agentFee) {
                                this.agentFee = agentFee;
                            }

                            public double getAgentPer() {
                                return agentPer;
                            }

                            public void setAgentPer(double agentPer) {
                                this.agentPer = agentPer;
                            }

                            public Object getPolicyCreateDate() {
                                return policyCreateDate;
                            }

                            public void setPolicyCreateDate(Object policyCreateDate) {
                                this.policyCreateDate = policyCreateDate;
                            }

                            public Object getPolicyNo() {
                                return policyNo;
                            }

                            public void setPolicyNo(Object policyNo) {
                                this.policyNo = policyNo;
                            }

                            public double getSpFee() {
                                return spFee;
                            }

                            public void setSpFee(double spFee) {
                                this.spFee = spFee;
                            }

                            public double getSpPer() {
                                return spPer;
                            }

                            public void setSpPer(double spPer) {
                                this.spPer = spPer;
                            }
                        }
                    }
                }

                public static class SegmentBean {
                    /**
                     * airlineCode : FU
                     * arrCode : HRB
                     * arrCodeName : null
                     * arrTime : 2019-05-31T23:00:00
                     * arriTerm : T2
                     * baseFare : 2580.0
                     * cabinClass : R
                     * cabinPrice : 444.0
                     * cabinState : OP
                     * checkInUserId : null
                     * depCode : FOC
                     * depCodeName : null
                     * depTerm : --
                     * depTime : 2019-05-31T17:30:00
                     * depTimeforTQ : 2019-05-31T00:00:00
                     * flightNo : FU6638
                     * id : null
                     * inventory : IBE
                     * lowstCabin : null
                     * luggageProductList : []
                     * mealProductList : []
                     * offerItemId : OW-TICKET-7fb36bbd110641399e590c927d19352e#OW-FOC-HRB-20190531-FU6638-WONLY-R
                     * orderId : null
                     * planeType : 738
                     * point : null
                     * referenceCabin :
                     * referenceFare : 0.0
                     * stopArrTime : null
                     * stopCity : ??????
                     * stopDepTime : null
                     * timeLimit : null
                     * tripType : ONEWAY
                     * yktProductList : []
                     */

                    private String airlineCode;
                    private String arrCode;
                    private Object arrCodeName;
                    private String arrTime;
                    private String arriTerm;
                    private double baseFare;
                    private String cabinClass;
                    private double cabinPrice;
                    private String cabinState;
                    private Object checkInUserId;
                    private String depCode;
                    private Object depCodeName;
                    private String depTerm;
                    private String depTime;
                    private String depTimeforTQ;
                    private String flightNo;
                    private Object id;
                    private String inventory;
                    private Object lowstCabin;
                    private String offerItemId;
                    private Object orderId;
                    private String planeType;
                    private Object point;
                    private String referenceCabin;
                    private double referenceFare;
                    private Object stopArrTime;
                    private String stopCity;
                    private Object stopDepTime;
                    private Object timeLimit;
                    private String tripType;
                    private List <?> luggageProductList;
                    private List <?> mealProductList;
                    private List <?> yktProductList;

                    public String getAirlineCode() {
                        return airlineCode;
                    }

                    public void setAirlineCode(String airlineCode) {
                        this.airlineCode = airlineCode;
                    }

                    public String getArrCode() {
                        return arrCode;
                    }

                    public void setArrCode(String arrCode) {
                        this.arrCode = arrCode;
                    }

                    public Object getArrCodeName() {
                        return arrCodeName;
                    }

                    public void setArrCodeName(Object arrCodeName) {
                        this.arrCodeName = arrCodeName;
                    }

                    public String getArrTime() {
                        return arrTime;
                    }

                    public void setArrTime(String arrTime) {
                        this.arrTime = arrTime;
                    }

                    public String getArriTerm() {
                        return arriTerm;
                    }

                    public void setArriTerm(String arriTerm) {
                        this.arriTerm = arriTerm;
                    }

                    public double getBaseFare() {
                        return baseFare;
                    }

                    public void setBaseFare(double baseFare) {
                        this.baseFare = baseFare;
                    }

                    public String getCabinClass() {
                        return cabinClass;
                    }

                    public void setCabinClass(String cabinClass) {
                        this.cabinClass = cabinClass;
                    }

                    public double getCabinPrice() {
                        return cabinPrice;
                    }

                    public void setCabinPrice(double cabinPrice) {
                        this.cabinPrice = cabinPrice;
                    }

                    public String getCabinState() {
                        return cabinState;
                    }

                    public void setCabinState(String cabinState) {
                        this.cabinState = cabinState;
                    }

                    public Object getCheckInUserId() {
                        return checkInUserId;
                    }

                    public void setCheckInUserId(Object checkInUserId) {
                        this.checkInUserId = checkInUserId;
                    }

                    public String getDepCode() {
                        return depCode;
                    }

                    public void setDepCode(String depCode) {
                        this.depCode = depCode;
                    }

                    public Object getDepCodeName() {
                        return depCodeName;
                    }

                    public void setDepCodeName(Object depCodeName) {
                        this.depCodeName = depCodeName;
                    }

                    public String getDepTerm() {
                        return depTerm;
                    }

                    public void setDepTerm(String depTerm) {
                        this.depTerm = depTerm;
                    }

                    public String getDepTime() {
                        return depTime;
                    }

                    public void setDepTime(String depTime) {
                        this.depTime = depTime;
                    }

                    public String getDepTimeforTQ() {
                        return depTimeforTQ;
                    }

                    public void setDepTimeforTQ(String depTimeforTQ) {
                        this.depTimeforTQ = depTimeforTQ;
                    }

                    public String getFlightNo() {
                        return flightNo;
                    }

                    public void setFlightNo(String flightNo) {
                        this.flightNo = flightNo;
                    }

                    public Object getId() {
                        return id;
                    }

                    public void setId(Object id) {
                        this.id = id;
                    }

                    public String getInventory() {
                        return inventory;
                    }

                    public void setInventory(String inventory) {
                        this.inventory = inventory;
                    }

                    public Object getLowstCabin() {
                        return lowstCabin;
                    }

                    public void setLowstCabin(Object lowstCabin) {
                        this.lowstCabin = lowstCabin;
                    }

                    public String getOfferItemId() {
                        return offerItemId;
                    }

                    public void setOfferItemId(String offerItemId) {
                        this.offerItemId = offerItemId;
                    }

                    public Object getOrderId() {
                        return orderId;
                    }

                    public void setOrderId(Object orderId) {
                        this.orderId = orderId;
                    }

                    public String getPlaneType() {
                        return planeType;
                    }

                    public void setPlaneType(String planeType) {
                        this.planeType = planeType;
                    }

                    public Object getPoint() {
                        return point;
                    }

                    public void setPoint(Object point) {
                        this.point = point;
                    }

                    public String getReferenceCabin() {
                        return referenceCabin;
                    }

                    public void setReferenceCabin(String referenceCabin) {
                        this.referenceCabin = referenceCabin;
                    }

                    public double getReferenceFare() {
                        return referenceFare;
                    }

                    public void setReferenceFare(double referenceFare) {
                        this.referenceFare = referenceFare;
                    }

                    public Object getStopArrTime() {
                        return stopArrTime;
                    }

                    public void setStopArrTime(Object stopArrTime) {
                        this.stopArrTime = stopArrTime;
                    }

                    public String getStopCity() {
                        return stopCity;
                    }

                    public void setStopCity(String stopCity) {
                        this.stopCity = stopCity;
                    }

                    public Object getStopDepTime() {
                        return stopDepTime;
                    }

                    public void setStopDepTime(Object stopDepTime) {
                        this.stopDepTime = stopDepTime;
                    }

                    public Object getTimeLimit() {
                        return timeLimit;
                    }

                    public void setTimeLimit(Object timeLimit) {
                        this.timeLimit = timeLimit;
                    }

                    public String getTripType() {
                        return tripType;
                    }

                    public void setTripType(String tripType) {
                        this.tripType = tripType;
                    }

                    public List <?> getLuggageProductList() {
                        return luggageProductList;
                    }

                    public void setLuggageProductList(List <?> luggageProductList) {
                        this.luggageProductList = luggageProductList;
                    }

                    public List <?> getMealProductList() {
                        return mealProductList;
                    }

                    public void setMealProductList(List <?> mealProductList) {
                        this.mealProductList = mealProductList;
                    }

                    public List <?> getYktProductList() {
                        return yktProductList;
                    }

                    public void setYktProductList(List <?> yktProductList) {
                        this.yktProductList = yktProductList;
                    }
                }

                public static class FareDetailsBean {
                    /**
                     * airportTax : 50.0
                     * commission : {"agentFee":0,"agentPer":0,"policyCreateDate":null,"policyNo":null,"spFee":0,"spPer":0}
                     * farePrice : 444.0
                     * fuelSurcharge : 0.0
                     * marketFare : 450.0
                     * netFare : 444.0
                     * otherCharges : 0.0
                     * passengerCount : 1
                     * passengerType : ADULT
                     * pnCMBCardMile : 0.0
                     * policyNo : null
                     * refrenceCabin : null
                     * refrenceFare : 0.0
                     * remark : null
                     * returnPoint : 0.0
                     * total : 494.0
                     */

                    private double airportTax;
                    private CommissionBeanXXX commission;
                    private double farePrice;
                    private double fuelSurcharge;
                    private double marketFare;
                    private double netFare;
                    private double otherCharges;
                    private int passengerCount;
                    private String passengerType;
                    private double pnCMBCardMile;
                    private Object policyNo;
                    private Object refrenceCabin;
                    private double refrenceFare;
                    private Object remark;
                    private double returnPoint;
                    private double total;

                    public double getAirportTax() {
                        return airportTax;
                    }

                    public void setAirportTax(double airportTax) {
                        this.airportTax = airportTax;
                    }

                    public CommissionBeanXXX getCommission() {
                        return commission;
                    }

                    public void setCommission(CommissionBeanXXX commission) {
                        this.commission = commission;
                    }

                    public double getFarePrice() {
                        return farePrice;
                    }

                    public void setFarePrice(double farePrice) {
                        this.farePrice = farePrice;
                    }

                    public double getFuelSurcharge() {
                        return fuelSurcharge;
                    }

                    public void setFuelSurcharge(double fuelSurcharge) {
                        this.fuelSurcharge = fuelSurcharge;
                    }

                    public double getMarketFare() {
                        return marketFare;
                    }

                    public void setMarketFare(double marketFare) {
                        this.marketFare = marketFare;
                    }

                    public double getNetFare() {
                        return netFare;
                    }

                    public void setNetFare(double netFare) {
                        this.netFare = netFare;
                    }

                    public double getOtherCharges() {
                        return otherCharges;
                    }

                    public void setOtherCharges(double otherCharges) {
                        this.otherCharges = otherCharges;
                    }

                    public int getPassengerCount() {
                        return passengerCount;
                    }

                    public void setPassengerCount(int passengerCount) {
                        this.passengerCount = passengerCount;
                    }

                    public String getPassengerType() {
                        return passengerType;
                    }

                    public void setPassengerType(String passengerType) {
                        this.passengerType = passengerType;
                    }

                    public double getPnCMBCardMile() {
                        return pnCMBCardMile;
                    }

                    public void setPnCMBCardMile(double pnCMBCardMile) {
                        this.pnCMBCardMile = pnCMBCardMile;
                    }

                    public Object getPolicyNo() {
                        return policyNo;
                    }

                    public void setPolicyNo(Object policyNo) {
                        this.policyNo = policyNo;
                    }

                    public Object getRefrenceCabin() {
                        return refrenceCabin;
                    }

                    public void setRefrenceCabin(Object refrenceCabin) {
                        this.refrenceCabin = refrenceCabin;
                    }

                    public double getRefrenceFare() {
                        return refrenceFare;
                    }

                    public void setRefrenceFare(double refrenceFare) {
                        this.refrenceFare = refrenceFare;
                    }

                    public Object getRemark() {
                        return remark;
                    }

                    public void setRemark(Object remark) {
                        this.remark = remark;
                    }

                    public double getReturnPoint() {
                        return returnPoint;
                    }

                    public void setReturnPoint(double returnPoint) {
                        this.returnPoint = returnPoint;
                    }

                    public double getTotal() {
                        return total;
                    }

                    public void setTotal(double total) {
                        this.total = total;
                    }

                    public static class CommissionBeanXXX {
                        /**
                         * agentFee : 0.0
                         * agentPer : 0.0
                         * policyCreateDate : null
                         * policyNo : null
                         * spFee : 0.0
                         * spPer : 0.0
                         */

                        private double agentFee;
                        private double agentPer;
                        private Object policyCreateDate;
                        private Object policyNo;
                        private double spFee;
                        private double spPer;

                        public double getAgentFee() {
                            return agentFee;
                        }

                        public void setAgentFee(double agentFee) {
                            this.agentFee = agentFee;
                        }

                        public double getAgentPer() {
                            return agentPer;
                        }

                        public void setAgentPer(double agentPer) {
                            this.agentPer = agentPer;
                        }

                        public Object getPolicyCreateDate() {
                            return policyCreateDate;
                        }

                        public void setPolicyCreateDate(Object policyCreateDate) {
                            this.policyCreateDate = policyCreateDate;
                        }

                        public Object getPolicyNo() {
                            return policyNo;
                        }

                        public void setPolicyNo(Object policyNo) {
                            this.policyNo = policyNo;
                        }

                        public double getSpFee() {
                            return spFee;
                        }

                        public void setSpFee(double spFee) {
                            this.spFee = spFee;
                        }

                        public double getSpPer() {
                            return spPer;
                        }

                        public void setSpPer(double spPer) {
                            this.spPer = spPer;
                        }
                    }
                }
            }
        }

        public static class FlightsBean {
            private List <SegmentsBean> segments;

            public List <SegmentsBean> getSegments() {
                return segments;
            }

            public void setSegments(List <SegmentsBean> segments) {
                this.segments = segments;
            }

            public static class SegmentsBean {
                /**
                 * aircraftStyle : 738
                 * airlineCode : FU
                 * arrCode : HRB
                 * arrTime : 2019-05-31 23:00
                 * arriTerm : null
                 * baseFare : null
                 * cabinCode : R
                 * cabinState : null
                 * changeRuleText : {"cabin":"R","changeTimeslots":[{"adultFare":444,"adultRate":1,"childFare":444,"childRate":1,"index":0,"infantFare":260,"infantRate":1},{"adultFare":444,"adultRate":1,"childFare":444,"childRate":1,"index":1,"infantFare":260,"infantRate":1},{"adultFare":444,"adultRate":1,"childFare":444,"childRate":1,"index":2,"infantFare":260,"infantRate":1},{"adultFare":444,"adultRate":1,"childFare":444,"childRate":1,"index":3,"infantFare":260,"infantRate":1}],"productCode":"YMD","refundTimeSlots":[{"adultFare":400,"adultRate":0.9,"childFare":400,"childRate":0.9,"index":0,"infantFare":0,"infantRate":0},{"adultFare":311,"adultRate":0.7,"childFare":311,"childRate":0.7,"index":1,"infantFare":0,"infantRate":0},{"adultFare":222,"adultRate":0.5,"childFare":222,"childRate":0.5,"index":2,"infantFare":0,"infantRate":0},{"adultFare":133,"adultRate":0.3,"childFare":133,"childRate":0.3,"index":3,"infantFare":0,"infantRate":0}],"remark":"??????????????????"}
                 * childMinus : 0
                 * couponIssDiscount : 0
                 * couponIssMark : null
                 * couponIssOrderNum : 0
                 * depCode : FOC
                 * depTerm : null
                 * depTime : 2019-05-31 17:30
                 * dstAirport : ?????????????????????
                 * flightNo : 6638
                 * index : 0
                 * inventory : 10
                 * markertFare : null
                 * offerItemId : null
                 * orgAirport : ??????????????????
                 * productCode : WONLY
                 * referenceCabin : null
                 * remarks : null
                 * stopArrTime :
                 * stopCity : ??????
                 * stopDepTime :
                 * stopsMsg : ??????,-,-,-|
                 */

                private String aircraftStyle;
                private String airlineCode;
                private String arrCode;
                private String arrTime;
                private Object arriTerm;
                private Object baseFare;
                private String cabinCode;
                private Object cabinState;
                private ChangeRuleTextBean changeRuleText;
                private int childMinus;
                private int couponIssDiscount;
                private Object couponIssMark;
                private int couponIssOrderNum;
                private String depCode;
                private Object depTerm;
                private String depTime;
                private String dstAirport;
                private String flightNo;
                private String index;
                private String inventory;
                private Object markertFare;
                private Object offerItemId;
                private String orgAirport;
                private String productCode;
                private Object referenceCabin;
                private Object remarks;
                private String stopArrTime;
                private String stopCity;
                private String stopDepTime;
                private String stopsMsg;

                public String getAircraftStyle() {
                    return aircraftStyle;
                }

                public void setAircraftStyle(String aircraftStyle) {
                    this.aircraftStyle = aircraftStyle;
                }

                public String getAirlineCode() {
                    return airlineCode;
                }

                public void setAirlineCode(String airlineCode) {
                    this.airlineCode = airlineCode;
                }

                public String getArrCode() {
                    return arrCode;
                }

                public void setArrCode(String arrCode) {
                    this.arrCode = arrCode;
                }

                public String getArrTime() {
                    return arrTime;
                }

                public void setArrTime(String arrTime) {
                    this.arrTime = arrTime;
                }

                public Object getArriTerm() {
                    return arriTerm;
                }

                public void setArriTerm(Object arriTerm) {
                    this.arriTerm = arriTerm;
                }

                public Object getBaseFare() {
                    return baseFare;
                }

                public void setBaseFare(Object baseFare) {
                    this.baseFare = baseFare;
                }

                public String getCabinCode() {
                    return cabinCode;
                }

                public void setCabinCode(String cabinCode) {
                    this.cabinCode = cabinCode;
                }

                public Object getCabinState() {
                    return cabinState;
                }

                public void setCabinState(Object cabinState) {
                    this.cabinState = cabinState;
                }

                public ChangeRuleTextBean getChangeRuleText() {
                    return changeRuleText;
                }

                public void setChangeRuleText(ChangeRuleTextBean changeRuleText) {
                    this.changeRuleText = changeRuleText;
                }

                public int getChildMinus() {
                    return childMinus;
                }

                public void setChildMinus(int childMinus) {
                    this.childMinus = childMinus;
                }

                public int getCouponIssDiscount() {
                    return couponIssDiscount;
                }

                public void setCouponIssDiscount(int couponIssDiscount) {
                    this.couponIssDiscount = couponIssDiscount;
                }

                public Object getCouponIssMark() {
                    return couponIssMark;
                }

                public void setCouponIssMark(Object couponIssMark) {
                    this.couponIssMark = couponIssMark;
                }

                public int getCouponIssOrderNum() {
                    return couponIssOrderNum;
                }

                public void setCouponIssOrderNum(int couponIssOrderNum) {
                    this.couponIssOrderNum = couponIssOrderNum;
                }

                public String getDepCode() {
                    return depCode;
                }

                public void setDepCode(String depCode) {
                    this.depCode = depCode;
                }

                public Object getDepTerm() {
                    return depTerm;
                }

                public void setDepTerm(Object depTerm) {
                    this.depTerm = depTerm;
                }

                public String getDepTime() {
                    return depTime;
                }

                public void setDepTime(String depTime) {
                    this.depTime = depTime;
                }

                public String getDstAirport() {
                    return dstAirport;
                }

                public void setDstAirport(String dstAirport) {
                    this.dstAirport = dstAirport;
                }

                public String getFlightNo() {
                    return flightNo;
                }

                public void setFlightNo(String flightNo) {
                    this.flightNo = flightNo;
                }

                public String getIndex() {
                    return index;
                }

                public void setIndex(String index) {
                    this.index = index;
                }

                public String getInventory() {
                    return inventory;
                }

                public void setInventory(String inventory) {
                    this.inventory = inventory;
                }

                public Object getMarkertFare() {
                    return markertFare;
                }

                public void setMarkertFare(Object markertFare) {
                    this.markertFare = markertFare;
                }

                public Object getOfferItemId() {
                    return offerItemId;
                }

                public void setOfferItemId(Object offerItemId) {
                    this.offerItemId = offerItemId;
                }

                public String getOrgAirport() {
                    return orgAirport;
                }

                public void setOrgAirport(String orgAirport) {
                    this.orgAirport = orgAirport;
                }

                public String getProductCode() {
                    return productCode;
                }

                public void setProductCode(String productCode) {
                    this.productCode = productCode;
                }

                public Object getReferenceCabin() {
                    return referenceCabin;
                }

                public void setReferenceCabin(Object referenceCabin) {
                    this.referenceCabin = referenceCabin;
                }

                public Object getRemarks() {
                    return remarks;
                }

                public void setRemarks(Object remarks) {
                    this.remarks = remarks;
                }

                public String getStopArrTime() {
                    return stopArrTime;
                }

                public void setStopArrTime(String stopArrTime) {
                    this.stopArrTime = stopArrTime;
                }

                public String getStopCity() {
                    return stopCity;
                }

                public void setStopCity(String stopCity) {
                    this.stopCity = stopCity;
                }

                public String getStopDepTime() {
                    return stopDepTime;
                }

                public void setStopDepTime(String stopDepTime) {
                    this.stopDepTime = stopDepTime;
                }

                public String getStopsMsg() {
                    return stopsMsg;
                }

                public void setStopsMsg(String stopsMsg) {
                    this.stopsMsg = stopsMsg;
                }

                public static class ChangeRuleTextBean {
                    /**
                     * cabin : R
                     * changeTimeslots : [{"adultFare":444,"adultRate":1,"childFare":444,"childRate":1,"index":0,"infantFare":260,"infantRate":1},{"adultFare":444,"adultRate":1,"childFare":444,"childRate":1,"index":1,"infantFare":260,"infantRate":1},{"adultFare":444,"adultRate":1,"childFare":444,"childRate":1,"index":2,"infantFare":260,"infantRate":1},{"adultFare":444,"adultRate":1,"childFare":444,"childRate":1,"index":3,"infantFare":260,"infantRate":1}]
                     * productCode : YMD
                     * refundTimeSlots : [{"adultFare":400,"adultRate":0.9,"childFare":400,"childRate":0.9,"index":0,"infantFare":0,"infantRate":0},{"adultFare":311,"adultRate":0.7,"childFare":311,"childRate":0.7,"index":1,"infantFare":0,"infantRate":0},{"adultFare":222,"adultRate":0.5,"childFare":222,"childRate":0.5,"index":2,"infantFare":0,"infantRate":0},{"adultFare":133,"adultRate":0.3,"childFare":133,"childRate":0.3,"index":3,"infantFare":0,"infantRate":0}]
                     * remark : ??????????????????
                     */

                    private String cabin;
                    private String productCode;
                    private String remark;
                    private List <ChangeTimeslotsBean> changeTimeslots;
                    private List <RefundTimeSlotsBean> refundTimeSlots;

                    public String getCabin() {
                        return cabin;
                    }

                    public void setCabin(String cabin) {
                        this.cabin = cabin;
                    }

                    public String getProductCode() {
                        return productCode;
                    }

                    public void setProductCode(String productCode) {
                        this.productCode = productCode;
                    }

                    public String getRemark() {
                        return remark;
                    }

                    public void setRemark(String remark) {
                        this.remark = remark;
                    }

                    public List <ChangeTimeslotsBean> getChangeTimeslots() {
                        return changeTimeslots;
                    }

                    public void setChangeTimeslots(List <ChangeTimeslotsBean> changeTimeslots) {
                        this.changeTimeslots = changeTimeslots;
                    }

                    public List <RefundTimeSlotsBean> getRefundTimeSlots() {
                        return refundTimeSlots;
                    }

                    public void setRefundTimeSlots(List <RefundTimeSlotsBean> refundTimeSlots) {
                        this.refundTimeSlots = refundTimeSlots;
                    }

                    public static class ChangeTimeslotsBean {
                        /**
                         * adultFare : 444
                         * adultRate : 1
                         * childFare : 444
                         * childRate : 1
                         * index : 0
                         * infantFare : 260
                         * infantRate : 1
                         */

                        private int adultFare;
                        private int adultRate;
                        private int childFare;
                        private int childRate;
                        private int index;
                        private int infantFare;
                        private int infantRate;

                        public int getAdultFare() {
                            return adultFare;
                        }

                        public void setAdultFare(int adultFare) {
                            this.adultFare = adultFare;
                        }

                        public int getAdultRate() {
                            return adultRate;
                        }

                        public void setAdultRate(int adultRate) {
                            this.adultRate = adultRate;
                        }

                        public int getChildFare() {
                            return childFare;
                        }

                        public void setChildFare(int childFare) {
                            this.childFare = childFare;
                        }

                        public int getChildRate() {
                            return childRate;
                        }

                        public void setChildRate(int childRate) {
                            this.childRate = childRate;
                        }

                        public int getIndex() {
                            return index;
                        }

                        public void setIndex(int index) {
                            this.index = index;
                        }

                        public int getInfantFare() {
                            return infantFare;
                        }

                        public void setInfantFare(int infantFare) {
                            this.infantFare = infantFare;
                        }

                        public int getInfantRate() {
                            return infantRate;
                        }

                        public void setInfantRate(int infantRate) {
                            this.infantRate = infantRate;
                        }
                    }

                    public static class RefundTimeSlotsBean {
                        /**
                         * adultFare : 400
                         * adultRate : 0.9
                         * childFare : 400
                         * childRate : 0.9
                         * index : 0
                         * infantFare : 0
                         * infantRate : 0.0
                         */

                        private int adultFare;
                        private double adultRate;
                        private int childFare;
                        private double childRate;
                        private int index;
                        private int infantFare;
                        private double infantRate;

                        public int getAdultFare() {
                            return adultFare;
                        }

                        public void setAdultFare(int adultFare) {
                            this.adultFare = adultFare;
                        }

                        public double getAdultRate() {
                            return adultRate;
                        }

                        public void setAdultRate(double adultRate) {
                            this.adultRate = adultRate;
                        }

                        public int getChildFare() {
                            return childFare;
                        }

                        public void setChildFare(int childFare) {
                            this.childFare = childFare;
                        }

                        public double getChildRate() {
                            return childRate;
                        }

                        public void setChildRate(double childRate) {
                            this.childRate = childRate;
                        }

                        public int getIndex() {
                            return index;
                        }

                        public void setIndex(int index) {
                            this.index = index;
                        }

                        public int getInfantFare() {
                            return infantFare;
                        }

                        public void setInfantFare(int infantFare) {
                            this.infantFare = infantFare;
                        }

                        public double getInfantRate() {
                            return infantRate;
                        }

                        public void setInfantRate(double infantRate) {
                            this.infantRate = infantRate;
                        }
                    }
                }
            }
        }

        public static class InsuranceProductsBean {
            /**
             * dtoCompany : {"airlineCode":null,"code":"FZBN","connectionType":null,"dtoInsurances":[],"dtoProducts":[],"iataNO":null,"id":null,"name":"????????????????????????????????????","officeNO":null,"password":null,"userName":null}
             * id : null
             * offerItemId : MTS-INSURANCE-2f782047918c49a29de645fcea346424#130160037#6717
             * premium : 30.0
             * productCode : MTS-INSURANCE-2f782047918c49a29de645fcea346424#130160037#6717#SRV0
             * productID : 6717
             * productName : ???????????????
             * protocolID : 130160037
             * protocolProductName : null
             * repay : 2700000.0
             */

            private DtoCompanyBean dtoCompany;
            private Object id;
            private String offerItemId;
            private double premium;
            private String productCode;
            private String productID;
            private String productName;
            private String protocolID;
            private Object protocolProductName;
            private double repay;

            public DtoCompanyBean getDtoCompany() {
                return dtoCompany;
            }

            public void setDtoCompany(DtoCompanyBean dtoCompany) {
                this.dtoCompany = dtoCompany;
            }

            public Object getId() {
                return id;
            }

            public void setId(Object id) {
                this.id = id;
            }

            public String getOfferItemId() {
                return offerItemId;
            }

            public void setOfferItemId(String offerItemId) {
                this.offerItemId = offerItemId;
            }

            public double getPremium() {
                return premium;
            }

            public void setPremium(double premium) {
                this.premium = premium;
            }

            public String getProductCode() {
                return productCode;
            }

            public void setProductCode(String productCode) {
                this.productCode = productCode;
            }

            public String getProductID() {
                return productID;
            }

            public void setProductID(String productID) {
                this.productID = productID;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public String getProtocolID() {
                return protocolID;
            }

            public void setProtocolID(String protocolID) {
                this.protocolID = protocolID;
            }

            public Object getProtocolProductName() {
                return protocolProductName;
            }

            public void setProtocolProductName(Object protocolProductName) {
                this.protocolProductName = protocolProductName;
            }

            public double getRepay() {
                return repay;
            }

            public void setRepay(double repay) {
                this.repay = repay;
            }

            public static class DtoCompanyBean {
                /**
                 * airlineCode : null
                 * code : FZBN
                 * connectionType : null
                 * dtoInsurances : []
                 * dtoProducts : []
                 * iataNO : null
                 * id : null
                 * name : ????????????????????????????????????
                 * officeNO : null
                 * password : null
                 * userName : null
                 */

                private Object airlineCode;
                private String code;
                private Object connectionType;
                private Object iataNO;
                private Object id;
                private String name;
                private Object officeNO;
                private Object password;
                private Object userName;
                private List <?> dtoInsurances;
                private List <?> dtoProducts;

                public Object getAirlineCode() {
                    return airlineCode;
                }

                public void setAirlineCode(Object airlineCode) {
                    this.airlineCode = airlineCode;
                }

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public Object getConnectionType() {
                    return connectionType;
                }

                public void setConnectionType(Object connectionType) {
                    this.connectionType = connectionType;
                }

                public Object getIataNO() {
                    return iataNO;
                }

                public void setIataNO(Object iataNO) {
                    this.iataNO = iataNO;
                }

                public Object getId() {
                    return id;
                }

                public void setId(Object id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public Object getOfficeNO() {
                    return officeNO;
                }

                public void setOfficeNO(Object officeNO) {
                    this.officeNO = officeNO;
                }

                public Object getPassword() {
                    return password;
                }

                public void setPassword(Object password) {
                    this.password = password;
                }

                public Object getUserName() {
                    return userName;
                }

                public void setUserName(Object userName) {
                    this.userName = userName;
                }

                public List <?> getDtoInsurances() {
                    return dtoInsurances;
                }

                public void setDtoInsurances(List <?> dtoInsurances) {
                    this.dtoInsurances = dtoInsurances;
                }

                public List <?> getDtoProducts() {
                    return dtoProducts;
                }

                public void setDtoProducts(List <?> dtoProducts) {
                    this.dtoProducts = dtoProducts;
                }
            }
        }

        public static class PassengersParamsBean {
            /**
             * airportTaxMoney : 0
             * allMoney : 0
             * arrCity : null
             * arrCountry : null
             * arrPost : null
             * arrProvince : null
             * arrStreet : null
             * birthday : 1983-11-30
             * bornDate : null
             * certificateNo : 350802198311306035
             * certificateType : NI
             * depCity : null
             * depCountry : null
             * depPost : null
             * depProvince : null
             * depStreet : null
             * disabledType : null
             * expiredDate : null
             * firstname : null
             * fuelTaxMoney : 0
             * gender : null
             * id : null
             * insurance1 : 0
             * insurance2 : null
             * insuranceAmount : 0
             * inuranceNumberAndType : null
             * isInsure : 0
             * issueCountry : null
             * lastname : null
             * mobilePhone : 13072660551
             * name : ??????
             * nationality : null
             * offerItemId :
             * offerItemId1 :
             * othername : null
             * passengerType : ADULT
             * passportno : null
             * remark : null
             * specialCertificateType : null
             * specialCertificateTypeNo : null
             * ticketMoney : 0
             * uniqueNo : a5b269f4-e668-46ec-800c-29ab011f0656
             * xcdPrintStatus : null
             */

            private String airportTaxMoney;
            private String allMoney;
            private Object arrCity;
            private Object arrCountry;
            private Object arrPost;
            private Object arrProvince;
            private Object arrStreet;
            private String birthday;
            private Object bornDate;
            private String certificateNo;
            private String certificateType;
            private Object depCity;
            private Object depCountry;
            private Object depPost;
            private Object depProvince;
            private Object depStreet;
            private Object disabledType;
            private Object expiredDate;
            private Object firstname;
            private String fuelTaxMoney;
            private Object gender;
            private Object id;
            private String insurance1;
            private Object insurance2;
            private String insuranceAmount;
            private Object inuranceNumberAndType;
            private String isInsure;
            private Object issueCountry;
            private Object lastname;
            private String mobilePhone;
            private String name;
            private Object nationality;
            private String offerItemId;
            private String offerItemId1;
            private Object othername;
            private String passengerType;
            private Object passportno;
            private Object remark;
            private Object specialCertificateType;
            private Object specialCertificateTypeNo;
            private String ticketMoney;
            private String uniqueNo;
            private Object xcdPrintStatus;

            public String getAirportTaxMoney() {
                return airportTaxMoney;
            }

            public void setAirportTaxMoney(String airportTaxMoney) {
                this.airportTaxMoney = airportTaxMoney;
            }

            public String getAllMoney() {
                return allMoney;
            }

            public void setAllMoney(String allMoney) {
                this.allMoney = allMoney;
            }

            public Object getArrCity() {
                return arrCity;
            }

            public void setArrCity(Object arrCity) {
                this.arrCity = arrCity;
            }

            public Object getArrCountry() {
                return arrCountry;
            }

            public void setArrCountry(Object arrCountry) {
                this.arrCountry = arrCountry;
            }

            public Object getArrPost() {
                return arrPost;
            }

            public void setArrPost(Object arrPost) {
                this.arrPost = arrPost;
            }

            public Object getArrProvince() {
                return arrProvince;
            }

            public void setArrProvince(Object arrProvince) {
                this.arrProvince = arrProvince;
            }

            public Object getArrStreet() {
                return arrStreet;
            }

            public void setArrStreet(Object arrStreet) {
                this.arrStreet = arrStreet;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public Object getBornDate() {
                return bornDate;
            }

            public void setBornDate(Object bornDate) {
                this.bornDate = bornDate;
            }

            public String getCertificateNo() {
                return certificateNo;
            }

            public void setCertificateNo(String certificateNo) {
                this.certificateNo = certificateNo;
            }

            public String getCertificateType() {
                return certificateType;
            }

            public void setCertificateType(String certificateType) {
                this.certificateType = certificateType;
            }

            public Object getDepCity() {
                return depCity;
            }

            public void setDepCity(Object depCity) {
                this.depCity = depCity;
            }

            public Object getDepCountry() {
                return depCountry;
            }

            public void setDepCountry(Object depCountry) {
                this.depCountry = depCountry;
            }

            public Object getDepPost() {
                return depPost;
            }

            public void setDepPost(Object depPost) {
                this.depPost = depPost;
            }

            public Object getDepProvince() {
                return depProvince;
            }

            public void setDepProvince(Object depProvince) {
                this.depProvince = depProvince;
            }

            public Object getDepStreet() {
                return depStreet;
            }

            public void setDepStreet(Object depStreet) {
                this.depStreet = depStreet;
            }

            public Object getDisabledType() {
                return disabledType;
            }

            public void setDisabledType(Object disabledType) {
                this.disabledType = disabledType;
            }

            public Object getExpiredDate() {
                return expiredDate;
            }

            public void setExpiredDate(Object expiredDate) {
                this.expiredDate = expiredDate;
            }

            public Object getFirstname() {
                return firstname;
            }

            public void setFirstname(Object firstname) {
                this.firstname = firstname;
            }

            public String getFuelTaxMoney() {
                return fuelTaxMoney;
            }

            public void setFuelTaxMoney(String fuelTaxMoney) {
                this.fuelTaxMoney = fuelTaxMoney;
            }

            public Object getGender() {
                return gender;
            }

            public void setGender(Object gender) {
                this.gender = gender;
            }

            public Object getId() {
                return id;
            }

            public void setId(Object id) {
                this.id = id;
            }

            public String getInsurance1() {
                return insurance1;
            }

            public void setInsurance1(String insurance1) {
                this.insurance1 = insurance1;
            }

            public Object getInsurance2() {
                return insurance2;
            }

            public void setInsurance2(Object insurance2) {
                this.insurance2 = insurance2;
            }

            public String getInsuranceAmount() {
                return insuranceAmount;
            }

            public void setInsuranceAmount(String insuranceAmount) {
                this.insuranceAmount = insuranceAmount;
            }

            public Object getInuranceNumberAndType() {
                return inuranceNumberAndType;
            }

            public void setInuranceNumberAndType(Object inuranceNumberAndType) {
                this.inuranceNumberAndType = inuranceNumberAndType;
            }

            public String getIsInsure() {
                return isInsure;
            }

            public void setIsInsure(String isInsure) {
                this.isInsure = isInsure;
            }

            public Object getIssueCountry() {
                return issueCountry;
            }

            public void setIssueCountry(Object issueCountry) {
                this.issueCountry = issueCountry;
            }

            public Object getLastname() {
                return lastname;
            }

            public void setLastname(Object lastname) {
                this.lastname = lastname;
            }

            public String getMobilePhone() {
                return mobilePhone;
            }

            public void setMobilePhone(String mobilePhone) {
                this.mobilePhone = mobilePhone;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Object getNationality() {
                return nationality;
            }

            public void setNationality(Object nationality) {
                this.nationality = nationality;
            }

            public String getOfferItemId() {
                return offerItemId;
            }

            public void setOfferItemId(String offerItemId) {
                this.offerItemId = offerItemId;
            }

            public String getOfferItemId1() {
                return offerItemId1;
            }

            public void setOfferItemId1(String offerItemId1) {
                this.offerItemId1 = offerItemId1;
            }

            public Object getOthername() {
                return othername;
            }

            public void setOthername(Object othername) {
                this.othername = othername;
            }

            public String getPassengerType() {
                return passengerType;
            }

            public void setPassengerType(String passengerType) {
                this.passengerType = passengerType;
            }

            public Object getPassportno() {
                return passportno;
            }

            public void setPassportno(Object passportno) {
                this.passportno = passportno;
            }

            public Object getRemark() {
                return remark;
            }

            public void setRemark(Object remark) {
                this.remark = remark;
            }

            public Object getSpecialCertificateType() {
                return specialCertificateType;
            }

            public void setSpecialCertificateType(Object specialCertificateType) {
                this.specialCertificateType = specialCertificateType;
            }

            public Object getSpecialCertificateTypeNo() {
                return specialCertificateTypeNo;
            }

            public void setSpecialCertificateTypeNo(Object specialCertificateTypeNo) {
                this.specialCertificateTypeNo = specialCertificateTypeNo;
            }

            public String getTicketMoney() {
                return ticketMoney;
            }

            public void setTicketMoney(String ticketMoney) {
                this.ticketMoney = ticketMoney;
            }

            public String getUniqueNo() {
                return uniqueNo;
            }

            public void setUniqueNo(String uniqueNo) {
                this.uniqueNo = uniqueNo;
            }

            public Object getXcdPrintStatus() {
                return xcdPrintStatus;
            }

            public void setXcdPrintStatus(Object xcdPrintStatus) {
                this.xcdPrintStatus = xcdPrintStatus;
            }
        }
    }
}
