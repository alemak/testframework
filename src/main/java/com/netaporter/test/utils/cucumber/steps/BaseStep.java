package com.netaporter.test.utils.cucumber.steps;

import com.netaporter.test.utils.cucumber.ScenarioSession;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import com.netaporter.test.utils.enums.RegionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
@SuppressWarnings("SpringContextConfigurationInspection")
/*//cucumber.xml should be specified in the projects using this class
@ContextConfiguration(locations = {"classpath:cucumber.xml"})*/
public abstract class BaseStep {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected ScenarioSession scenarioSession;

   /*
   * For use in the scenario steps where channel ID needs to be embedded in data
   * */
    public static final String channelIdPlaceholder = "GET_CHANNEL_FROM_ENV_VAR";
   /*
     Read channel data from system property "channel"
   */
    protected int channelId = System.getProperty("channel")==null?
            0: SalesChannelEnum.valueOf(System.getProperty("channel")).getId();

    /*
     Read region data from system property "region"
   */
    protected RegionEnum region = System.getProperty("region")==null?
            null :
            RegionEnum.valueOf(System.getProperty("region"));

    /*
    Substitute the channel variable for the actual channel id from system property
    */
    public String embedVarInString(String str){
        return str.replace(channelIdPlaceholder, String.valueOf(channelId));
    }

}
