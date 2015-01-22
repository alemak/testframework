package com.netaporter.test.utils.enums;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Business-defined channels, a combination of "website" and "region".
 *
 * @author juanuys
 * <ul>
 * <li>Refactored: moved here from exporter</li>
 * <li>Added Website.class</li>
 * <li>Added Region.class</li>
 * </ul>
 *
 * Original Javadoc below:
 *
 * This Enum Class defines the different channels
 *
 * Created by: eduardo
 * Date: 16-Jul-2009
 *
 *
 * <p>
 * <b>Extremely important note below:</b>
 * <pre>Hi Juan,
Those values (1-4) were agreed with Edialog. I'm afraid but you can't get rid of the id. :(
Cheers
Eduardo

Juan Uys wrote:
Hey Edu,

(I'm copying Witter - didn't you also do EDialog stuff?)

The ExportChannel has these:

NAP_INTL (1,"nap-intl"),
NAP_AM (2,"nap-am"),
OUT_INTL (3,"outnet-intl"),
OUT_AM (4,"outnet-am");

The only classes using getId() is the EDIalog stuff when it constructs a line in the buffer.

So, is the values 1-4 significant, i.e. something we agreed with EDIalog? Or can I get rid of "id" and just pass them the ordinal?

Thanks,
Juan</pre>
 *
 *
 */
public enum SalesChannelEnum {

    NAP_INTL (1, "nap-intl", WebsiteEnum.NAP, RegionEnum.INTL),
    NAP_AM (2, "nap-am", WebsiteEnum.NAP, RegionEnum.AM),
    NAP_APAC (9, "nap-apac", WebsiteEnum.NAP, RegionEnum.APAC),

    OUT_INTL (3, "out-intl", WebsiteEnum.OUT, RegionEnum.INTL),
    OUT_AM (4, "out-am", WebsiteEnum.OUT, RegionEnum.AM),
    OUT_APAC (10, "out-apac", WebsiteEnum.OUT, RegionEnum.APAC),

    MRP_INTL(5, "mrp-intl", WebsiteEnum.MRP, RegionEnum.INTL),
    MRP_AM(6, "mrp-am", WebsiteEnum.MRP, RegionEnum.AM),
    MRP_APAC(111, "mrp-apac", WebsiteEnum.MRP, RegionEnum.APAC), // oh, dear -- stg-nap-intl stole my ID :-(

    STG_NAP_INTL (11, "stg-nap-intl", WebsiteEnum.NAP, RegionEnum.INTL),
    STG_NAP_AM (12, "stg-nap-am", WebsiteEnum.NAP, RegionEnum.AM),
    STG_OUT_INTL (13, "stg-out-intl", WebsiteEnum.OUT, RegionEnum.INTL),
    STG_OUT_AM (14, "stg-out-am", WebsiteEnum.OUT, RegionEnum.AM),
    STG_MRP_INTL(15, "stg-mrp-intl", WebsiteEnum.MRP, RegionEnum.INTL),
    STG_MRP_AM(16, "stg-mrp-am", WebsiteEnum.MRP, RegionEnum.AM),

    // APACS
    STG_NAP_APAC(17, "stg-nap-apac", WebsiteEnum.NAP, RegionEnum.APAC);


    private final int id;
    private final String name;
    private final WebsiteEnum website;
    private final RegionEnum region;

    SalesChannelEnum(int id, String name, WebsiteEnum website, RegionEnum region){
        this.id = id;
        this.name = name;
        this.website = website;
        this.region = region;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public WebsiteEnum getWebsite() {
        return website;
    }

    public RegionEnum getRegion() {
        return region;
    }

    public boolean isAProductionChannel() {
        return !getName().startsWith("stg-");
    }

    public Environment getEnvironment() {
        return !isAProductionChannel() ? Environment.STAGING : Environment.LIVE;
    }

    /**
     * Lookup by website.
     */
    private static final Map<WebsiteEnum, List<SalesChannelEnum>> lookupByWebsite = new HashMap<WebsiteEnum, List<SalesChannelEnum>>();
    static {
        for (SalesChannelEnum s : SalesChannelEnum.values()) {
            if (lookupByWebsite.get(s.getWebsite()) == null) {
                lookupByWebsite.put(s.getWebsite(), new ArrayList<SalesChannelEnum>(2));
            }
            lookupByWebsite.get(s.getWebsite()).add(s);
        }
    }
    public static List<SalesChannelEnum> getByWebsite(WebsiteEnum website) {
        return lookupByWebsite.get(website);
    }

    /**
     * Lookup by region.
     */
    private static final Map<RegionEnum, List<SalesChannelEnum>> lookupByRegion = new HashMap<RegionEnum, List<SalesChannelEnum>>();
    static {
        for (SalesChannelEnum s : SalesChannelEnum.values()) {
            if (lookupByRegion.get(s.getRegion()) == null) {
                lookupByRegion.put(s.getRegion(), new ArrayList<SalesChannelEnum>(3));
            }
            lookupByRegion.get(s.getRegion()).add(s);
        }
    }
    public static List<SalesChannelEnum> getByRegion(RegionEnum region) {
        return lookupByRegion.get(region);
    }

    static class Tuple {
        WebsiteEnum website;
        RegionEnum region;

        static Tuple t(WebsiteEnum website, RegionEnum region) {
            return new Tuple(website, region);
        }

        Tuple(WebsiteEnum website, RegionEnum region) {
            this.website = website;
            this.region = region;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tuple tuple = (Tuple) o;

            if (region != tuple.region) return false;
            if (website != tuple.website) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = website != null ? website.hashCode() : 0;
            result = 31 * result + (region != null ? region.hashCode() : 0);
            return result;
        }
    }
    /**
     * Lookup by website AND region (because ID isn't reliable. See below).
     */
    private static final Map<Tuple, SalesChannelEnum> lookupByWebsiteAndRegion = new HashMap<Tuple, SalesChannelEnum>();
    static {
        for (SalesChannelEnum s : SalesChannelEnum.values()) {
            if (s.isAProductionChannel())
                lookupByWebsiteAndRegion.put(Tuple.t(s.getWebsite(), s.getRegion()), s);
        }
    }
    public static SalesChannelEnum getByWebsiteAndRegion(WebsiteEnum website, RegionEnum region) {
        return lookupByWebsiteAndRegion.get(Tuple.t(website, region));
    }

    /**
     * Lookup By Channel Id
     *
     * (unfortunately, this isn't reliable, because stg-nap-intl CLEARLY doesn't have ID 11. Have a look at xt_central.channel, and you'r see MRP_APAC is 11. (Juan))
     */
    private static final Map<Integer, SalesChannelEnum> lookupById = new HashMap();
    static {
        for (SalesChannelEnum s : SalesChannelEnum.values()) {
            lookupById.put(s.id, s);
        }
    }
    public static SalesChannelEnum getById(Integer id) {
        return lookupById.get(id);
    }


    /**
     * return the Upper Case name of the Channel
     *
     * @return
     */
    public String getNameUpperCase(){
        return name.toUpperCase();
    }

    public boolean isNap() {
        return website == WebsiteEnum.NAP;
    }

    public boolean isOutnet() {
        return website == WebsiteEnum.OUT;
    }

    public boolean isMrPorter() {
        return website == WebsiteEnum.MRP;
    }

    public boolean isIntl() {
        return region == RegionEnum.INTL;
    }

    public boolean isAm() {
        return region == RegionEnum.AM;
    }

    public boolean isApac(){
        return region == RegionEnum.APAC;
    }
    public int getEDialogChannelId() {
        return id;
    }


    /**
     * Obtain the Channel associated to a string describing the channel.
     * String must be nap-intl, nap-am, outnet-intl,outnet-am otherwise returns null
     * @param channel
     * @return
     *
     * @deprecated Use {@link SalesChannelEnum#valueOf(String)} instead. In the same vein, use "NAP_INTL" in configs, instead of "nap-intl".
     */
    @Deprecated
    public static SalesChannelEnum getChannel(String channel) {
        if (channel != null) {
            String[] split = channel.split("-");
            Assert.isTrue(split.length == 2);
            String name1 = split[0].toUpperCase();
            if (name1.equals("OUTNET")) { // oh, joy.
                name1 = "OUT";
            } else if (name1.equals("MRPORTER")) { // oh, joy.
                name1 = "MRP";
            }
            try {
                WebsiteEnum website = WebsiteEnum.valueOf(name1);
                RegionEnum region = RegionEnum.valueOf(split[1].toUpperCase());
                return getByWebsiteAndRegion(website, region);
            } catch (IllegalArgumentException e) {
                return null;
            }
        } else return null;
    }

    public String getStockLocationId() {
        switch(region) {
            case AM:
                return "DC2";
            case INTL:
                return "DC1";
            case APAC:
                return "DC3";
            default:
                return "DC1";
        }
    }

    /**
     * Represents the environment the sales channel can exist on. Note: anything other than Staging is considered "Live".
     */
    public static enum Environment {
        STAGING, LIVE;
    }
}
