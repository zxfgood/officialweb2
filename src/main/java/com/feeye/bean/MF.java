package com.feeye.bean;


import java.util.List;

public class MF {

    /**
     * shoppingPreference : {"ancillaryPreferences":[{"productType":"Insurance"}]}
     * itineraries : [{"departureDate":"2019-03-22","origin":{"airport":{"code":"XMN"}},"destination":{"airport":{"code":"PEK"}},"segments":[{"id":"MF8101-XMN-PEK-2019-03-22","duration":"PT2H50M","departure":{"aircraftScheduledDateTime":"2019-03-22T11:00:00","timeZone":{"code":"CN00","designator":"+08:00","offset":480},"iataLocationCode":"XMN","iataLocationName":"厦门","stationName":"高崎","terminalName":"T3","compare":{"date":"2019.03.22","timezone":"+08:00"}},"arrival":{"aircraftScheduledDateTime":"2019-03-22T13:50:00","timeZone":{"code":"CN00","designator":"+08:00","offset":480},"iataLocationCode":"PEK","iataLocationName":"北京","stationName":"首都","terminalName":"T2"},"marketingCarrier":{"carrier":{"code":"MF"},"flightNumber":"8101"},"operatingCarrier":{"carrier":{"code":"MF"},"flightNumber":"8101"},"regionCode":"D","equipment":{"code":"789","manufacture":"BOEING"},"plusorminus":0,"during":{"hour":2,"min":50},"sellingClass":{"code":"H"}}]}]
     * passengers : [{"id":"ADT1","passengerType":"ADT","anonymousInd":true}]
     * passengerCount : {"adult":0,"child":0,"infant":0}
     */

    private ShoppingPreferenceBean shoppingPreference;
    private PassengerCountBean passengerCount;
    private List <ItinerariesBean> itineraries;
    private List <PassengersBean> passengers;

    public ShoppingPreferenceBean getShoppingPreference() {
        return shoppingPreference;
    }

    public void setShoppingPreference(ShoppingPreferenceBean shoppingPreference) {
        this.shoppingPreference = shoppingPreference;
    }

    public PassengerCountBean getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(PassengerCountBean passengerCount) {
        this.passengerCount = passengerCount;
    }

    public List <ItinerariesBean> getItineraries() {
        return itineraries;
    }

    public void setItineraries(List <ItinerariesBean> itineraries) {
        this.itineraries = itineraries;
    }

    public List <PassengersBean> getPassengers() {
        return passengers;
    }

    public void setPassengers(List <PassengersBean> passengers) {
        this.passengers = passengers;
    }

    public static class ShoppingPreferenceBean {
        private List <AncillaryPreferencesBean> ancillaryPreferences;

        public List <AncillaryPreferencesBean> getAncillaryPreferences() {
            return ancillaryPreferences;
        }

        public void setAncillaryPreferences(List <AncillaryPreferencesBean> ancillaryPreferences) {
            this.ancillaryPreferences = ancillaryPreferences;
        }

        public static class AncillaryPreferencesBean {
            /**
             * productType : Insurance
             */

            private String productType;

            public String getProductType() {
                return productType;
            }

            public void setProductType(String productType) {
                this.productType = productType;
            }
        }
    }

    public static class PassengerCountBean {
        /**
         * adult : 0
         * child : 0
         * infant : 0
         */

        private int adult;
        private int child;
        private int infant;

        public int getAdult() {
            return adult;
        }

        public void setAdult(int adult) {
            this.adult = adult;
        }

        public int getChild() {
            return child;
        }

        public void setChild(int child) {
            this.child = child;
        }

        public int getInfant() {
            return infant;
        }

        public void setInfant(int infant) {
            this.infant = infant;
        }
    }

    public static class ItinerariesBean {
        /**
         * departureDate : 2019-03-22
         * origin : {"airport":{"code":"XMN"}}
         * destination : {"airport":{"code":"PEK"}}
         * segments : [{"id":"MF8101-XMN-PEK-2019-03-22","duration":"PT2H50M","departure":{"aircraftScheduledDateTime":"2019-03-22T11:00:00","timeZone":{"code":"CN00","designator":"+08:00","offset":480},"iataLocationCode":"XMN","iataLocationName":"厦门","stationName":"高崎","terminalName":"T3","compare":{"date":"2019.03.22","timezone":"+08:00"}},"arrival":{"aircraftScheduledDateTime":"2019-03-22T13:50:00","timeZone":{"code":"CN00","designator":"+08:00","offset":480},"iataLocationCode":"PEK","iataLocationName":"北京","stationName":"首都","terminalName":"T2"},"marketingCarrier":{"carrier":{"code":"MF"},"flightNumber":"8101"},"operatingCarrier":{"carrier":{"code":"MF"},"flightNumber":"8101"},"regionCode":"D","equipment":{"code":"789","manufacture":"BOEING"},"plusorminus":0,"during":{"hour":2,"min":50},"sellingClass":{"code":"H"}}]
         */

        private String departureDate;
        private OriginBean origin;
        private DestinationBean destination;
        private List <SegmentsBean> segments;

        public String getDepartureDate() {
            return departureDate;
        }

        public void setDepartureDate(String departureDate) {
            this.departureDate = departureDate;
        }

        public OriginBean getOrigin() {
            return origin;
        }

        public void setOrigin(OriginBean origin) {
            this.origin = origin;
        }

        public DestinationBean getDestination() {
            return destination;
        }

        public void setDestination(DestinationBean destination) {
            this.destination = destination;
        }

        public List <SegmentsBean> getSegments() {
            return segments;
        }

        public void setSegments(List <SegmentsBean> segments) {
            this.segments = segments;
        }

        public static class OriginBean {
            /**
             * airport : {"code":"XMN"}
             */

            private AirportBean airport;

            public AirportBean getAirport() {
                return airport;
            }

            public void setAirport(AirportBean airport) {
                this.airport = airport;
            }

            public static class AirportBean {
                /**
                 * code : XMN
                 */

                private String code;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }
            }
        }

        public static class DestinationBean {
            /**
             * airport : {"code":"PEK"}
             */

            private AirportBeanX airport;

            public AirportBeanX getAirport() {
                return airport;
            }

            public void setAirport(AirportBeanX airport) {
                this.airport = airport;
            }

            public static class AirportBeanX {
                /**
                 * code : PEK
                 */

                private String code;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }
            }
        }

        public static class SegmentsBean {
            /**
             * id : MF8101-XMN-PEK-2019-03-22
             * duration : PT2H50M
             * departure : {"aircraftScheduledDateTime":"2019-03-22T11:00:00","timeZone":{"code":"CN00","designator":"+08:00","offset":480},"iataLocationCode":"XMN","iataLocationName":"厦门","stationName":"高崎","terminalName":"T3","compare":{"date":"2019.03.22","timezone":"+08:00"}}
             * arrival : {"aircraftScheduledDateTime":"2019-03-22T13:50:00","timeZone":{"code":"CN00","designator":"+08:00","offset":480},"iataLocationCode":"PEK","iataLocationName":"北京","stationName":"首都","terminalName":"T2"}
             * marketingCarrier : {"carrier":{"code":"MF"},"flightNumber":"8101"}
             * operatingCarrier : {"carrier":{"code":"MF"},"flightNumber":"8101"}
             * regionCode : D
             * equipment : {"code":"789","manufacture":"BOEING"}
             * plusorminus : 0
             * during : {"hour":2,"min":50}
             * sellingClass : {"code":"H"}
             */

            private String id;
            private String duration;
            private DepartureBean departure;
            private ArrivalBean arrival;
            private MarketingCarrierBean marketingCarrier;
            private OperatingCarrierBean operatingCarrier;
            private String regionCode;
            private EquipmentBean equipment;
            private int plusorminus;
            private DuringBean during;
            private SellingClassBean sellingClass;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public DepartureBean getDeparture() {
                return departure;
            }

            public void setDeparture(DepartureBean departure) {
                this.departure = departure;
            }

            public ArrivalBean getArrival() {
                return arrival;
            }

            public void setArrival(ArrivalBean arrival) {
                this.arrival = arrival;
            }

            public MarketingCarrierBean getMarketingCarrier() {
                return marketingCarrier;
            }

            public void setMarketingCarrier(MarketingCarrierBean marketingCarrier) {
                this.marketingCarrier = marketingCarrier;
            }

            public OperatingCarrierBean getOperatingCarrier() {
                return operatingCarrier;
            }

            public void setOperatingCarrier(OperatingCarrierBean operatingCarrier) {
                this.operatingCarrier = operatingCarrier;
            }

            public String getRegionCode() {
                return regionCode;
            }

            public void setRegionCode(String regionCode) {
                this.regionCode = regionCode;
            }

            public EquipmentBean getEquipment() {
                return equipment;
            }

            public void setEquipment(EquipmentBean equipment) {
                this.equipment = equipment;
            }

            public int getPlusorminus() {
                return plusorminus;
            }

            public void setPlusorminus(int plusorminus) {
                this.plusorminus = plusorminus;
            }

            public DuringBean getDuring() {
                return during;
            }

            public void setDuring(DuringBean during) {
                this.during = during;
            }

            public SellingClassBean getSellingClass() {
                return sellingClass;
            }

            public void setSellingClass(SellingClassBean sellingClass) {
                this.sellingClass = sellingClass;
            }

            public static class DepartureBean {
                /**
                 * aircraftScheduledDateTime : 2019-03-22T11:00:00
                 * timeZone : {"code":"CN00","designator":"+08:00","offset":480}
                 * iataLocationCode : XMN
                 * iataLocationName : 厦门
                 * stationName : 高崎
                 * terminalName : T3
                 * compare : {"date":"2019.03.22","timezone":"+08:00"}
                 */

                private String aircraftScheduledDateTime;
                private TimeZoneBean timeZone;
                private String iataLocationCode;
                private String iataLocationName;
                private String stationName;
                private String terminalName;
                private CompareBean compare;

                public String getAircraftScheduledDateTime() {
                    return aircraftScheduledDateTime;
                }

                public void setAircraftScheduledDateTime(String aircraftScheduledDateTime) {
                    this.aircraftScheduledDateTime = aircraftScheduledDateTime;
                }

                public TimeZoneBean getTimeZone() {
                    return timeZone;
                }

                public void setTimeZone(TimeZoneBean timeZone) {
                    this.timeZone = timeZone;
                }

                public String getIataLocationCode() {
                    return iataLocationCode;
                }

                public void setIataLocationCode(String iataLocationCode) {
                    this.iataLocationCode = iataLocationCode;
                }

                public String getIataLocationName() {
                    return iataLocationName;
                }

                public void setIataLocationName(String iataLocationName) {
                    this.iataLocationName = iataLocationName;
                }

                public String getStationName() {
                    return stationName;
                }

                public void setStationName(String stationName) {
                    this.stationName = stationName;
                }

                public String getTerminalName() {
                    return terminalName;
                }

                public void setTerminalName(String terminalName) {
                    this.terminalName = terminalName;
                }

                public CompareBean getCompare() {
                    return compare;
                }

                public void setCompare(CompareBean compare) {
                    this.compare = compare;
                }

                public static class TimeZoneBean {
                    /**
                     * code : CN00
                     * designator : +08:00
                     * offset : 480
                     */

                    private String code;
                    private String designator;
                    private int offset;

                    public String getCode() {
                        return code;
                    }

                    public void setCode(String code) {
                        this.code = code;
                    }

                    public String getDesignator() {
                        return designator;
                    }

                    public void setDesignator(String designator) {
                        this.designator = designator;
                    }

                    public int getOffset() {
                        return offset;
                    }

                    public void setOffset(int offset) {
                        this.offset = offset;
                    }
                }

                public static class CompareBean {
                    /**
                     * date : 2019.03.22
                     * timezone : +08:00
                     */

                    private String date;
                    private String timezone;

                    public String getDate() {
                        return date;
                    }

                    public void setDate(String date) {
                        this.date = date;
                    }

                    public String getTimezone() {
                        return timezone;
                    }

                    public void setTimezone(String timezone) {
                        this.timezone = timezone;
                    }
                }
            }

            public static class ArrivalBean {
                /**
                 * aircraftScheduledDateTime : 2019-03-22T13:50:00
                 * timeZone : {"code":"CN00","designator":"+08:00","offset":480}
                 * iataLocationCode : PEK
                 * iataLocationName : 北京
                 * stationName : 首都
                 * terminalName : T2
                 */

                private String aircraftScheduledDateTime;
                private TimeZoneBeanX timeZone;
                private String iataLocationCode;
                private String iataLocationName;
                private String stationName;
                private String terminalName;

                public String getAircraftScheduledDateTime() {
                    return aircraftScheduledDateTime;
                }

                public void setAircraftScheduledDateTime(String aircraftScheduledDateTime) {
                    this.aircraftScheduledDateTime = aircraftScheduledDateTime;
                }

                public TimeZoneBeanX getTimeZone() {
                    return timeZone;
                }

                public void setTimeZone(TimeZoneBeanX timeZone) {
                    this.timeZone = timeZone;
                }

                public String getIataLocationCode() {
                    return iataLocationCode;
                }

                public void setIataLocationCode(String iataLocationCode) {
                    this.iataLocationCode = iataLocationCode;
                }

                public String getIataLocationName() {
                    return iataLocationName;
                }

                public void setIataLocationName(String iataLocationName) {
                    this.iataLocationName = iataLocationName;
                }

                public String getStationName() {
                    return stationName;
                }

                public void setStationName(String stationName) {
                    this.stationName = stationName;
                }

                public String getTerminalName() {
                    return terminalName;
                }

                public void setTerminalName(String terminalName) {
                    this.terminalName = terminalName;
                }

                public static class TimeZoneBeanX {
                    /**
                     * code : CN00
                     * designator : +08:00
                     * offset : 480
                     */

                    private String code;
                    private String designator;
                    private int offset;

                    public String getCode() {
                        return code;
                    }

                    public void setCode(String code) {
                        this.code = code;
                    }

                    public String getDesignator() {
                        return designator;
                    }

                    public void setDesignator(String designator) {
                        this.designator = designator;
                    }

                    public int getOffset() {
                        return offset;
                    }

                    public void setOffset(int offset) {
                        this.offset = offset;
                    }
                }
            }

            public static class MarketingCarrierBean {
                /**
                 * carrier : {"code":"MF"}
                 * flightNumber : 8101
                 */

                private CarrierBean carrier;
                private String flightNumber;

                public CarrierBean getCarrier() {
                    return carrier;
                }

                public void setCarrier(CarrierBean carrier) {
                    this.carrier = carrier;
                }

                public String getFlightNumber() {
                    return flightNumber;
                }

                public void setFlightNumber(String flightNumber) {
                    this.flightNumber = flightNumber;
                }

                public static class CarrierBean {
                    /**
                     * code : MF
                     */

                    private String code;

                    public String getCode() {
                        return code;
                    }

                    public void setCode(String code) {
                        this.code = code;
                    }
                }
            }

            public static class OperatingCarrierBean {
                /**
                 * carrier : {"code":"MF"}
                 * flightNumber : 8101
                 */

                private CarrierBeanX carrier;
                private String flightNumber;

                public CarrierBeanX getCarrier() {
                    return carrier;
                }

                public void setCarrier(CarrierBeanX carrier) {
                    this.carrier = carrier;
                }

                public String getFlightNumber() {
                    return flightNumber;
                }

                public void setFlightNumber(String flightNumber) {
                    this.flightNumber = flightNumber;
                }

                public static class CarrierBeanX {
                    /**
                     * code : MF
                     */

                    private String code;

                    public String getCode() {
                        return code;
                    }

                    public void setCode(String code) {
                        this.code = code;
                    }
                }
            }

            public static class EquipmentBean {
                /**
                 * code : 789
                 * manufacture : BOEING
                 */

                private String code;
                private String manufacture;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getManufacture() {
                    return manufacture;
                }

                public void setManufacture(String manufacture) {
                    this.manufacture = manufacture;
                }
            }

            public static class DuringBean {
                /**
                 * hour : 2
                 * min : 50
                 */

                private int hour;
                private int min;

                public int getHour() {
                    return hour;
                }

                public void setHour(int hour) {
                    this.hour = hour;
                }

                public int getMin() {
                    return min;
                }

                public void setMin(int min) {
                    this.min = min;
                }
            }

            public static class SellingClassBean {
                /**
                 * code : H
                 */

                private String code;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }
            }
        }
    }

    public static class PassengersBean {
        /**
         * id : ADT1
         * passengerType : ADT
         * anonymousInd : true
         */

        private String id;
        private String passengerType;
        private boolean anonymousInd;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPassengerType() {
            return passengerType;
        }

        public void setPassengerType(String passengerType) {
            this.passengerType = passengerType;
        }

        public boolean isAnonymousInd() {
            return anonymousInd;
        }

        public void setAnonymousInd(boolean anonymousInd) {
            this.anonymousInd = anonymousInd;
        }
    }
}
