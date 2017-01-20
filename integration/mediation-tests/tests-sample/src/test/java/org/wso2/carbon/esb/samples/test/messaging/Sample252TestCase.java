/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.esb.samples.test.messaging;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageProducer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.common.FileManager;
import org.wso2.esb.integration.common.utils.servers.ActiveMQServer;
import org.wso2.esb.integration.common.utils.servers.axis2.SampleAxis2Server;

import java.io.File;

public class Sample252TestCase extends ESBIntegrationTest {

    private LogViewerClient logViewerClient = null;

    private final String MTOM_SERVICE = "MTOMSwASampleService";
    private SampleAxis2Server axis2Server;

    @BeforeClass(alwaysRun = true)
    public void startJMSBrokerAndConfigureESB() throws Exception {

        super.init();
        context = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);
        super.init();
        loadSampleESBConfiguration(252);

        logViewerClient =
                new LogViewerClient(contextUrls.getBackEndUrl(), getSessionCookie());

        axis2Server = new SampleAxis2Server("test_axis2_server_9001.xml");
        axis2Server.start();
        axis2Server.deployService(MTOM_SERVICE);

    }

    @AfterClass(alwaysRun = true)
    public void close() throws Exception {
        //reverting the changes done to esb sever
        Thread.sleep(10000); //let server to clear the artifact undeployment
        axis2Server.stop();
        super.cleanup();
    }


    private JMSBrokerConfiguration getJMSBrokerConfiguration() {
        return JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Pure Text (Binary) and POX Message Support with JMS " +
            "send simple payload to JS rule")
    public void testJMStoPOX() throws Exception {

        logViewerClient.clearLogs();

        JMSQueueMessageProducer sender = new JMSQueueMessageProducer(getJMSBrokerConfiguration());
        String queueName = "JMSTextProxy";
        try {
            sender.connect(queueName);
            for (int i = 0; i < 3; i++) {
                sender.pushMessage("12.33 1000 ACP");
            }
        } finally {
            sender.disconnect();
        }

        Thread.sleep(5000);

        LogEvent[] getLogsInfo = logViewerClient.getAllSystemLogs();
        boolean assertValue = false;
        for (LogEvent event : getLogsInfo) {
            if (event.getMessage().contains("<symbol>ACP</symbol>")) {
                assertValue = true;
                break;
            }
        }

        Assert.assertTrue(assertValue,
                "JMS message not received at the back end");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Pure Text (Binary) and POX Message Support with JMS " +
            "testJMSwithByteMessage")
    public void testJMSwithByteMessage() throws Exception {

        String gifFile = FrameworkPathUtil.getSystemResourceLocation() + File.separator + "artifacts"
                + File.separator + "ESB" + File.separator + "gif" + File.separator + "asf-logo.gif";

        JMSQueueMessageProducer sender = new JMSQueueMessageProducer(getJMSBrokerConfiguration());
        String queueName = "JMSFileUploadProxy";
        try {

            sender.connect(queueName);
            sender.sendBytesMessage(FileManager.getBytesFromFile(gifFile));

        } finally {
            sender.disconnect();
        }

        String gifReceivedContent = "R0lGODlhgwFkAPcAAPfrfmZmZpmZmczMzJkAM7SMb6xsSm" +
                "ZmM6xPRf9mZplmmf///66rkJmRT8wAZswAM/bbe7Y3TzMAM5ant5kAZplmZlBQUJlmM7OYkjMzM8zMZk" +
                "5ARc+DWZmZZuovWOYAVMgASn9/f7TDz7cAVGZmmae2xWYAZnWBjP/MZpkzZmYAM65KbejddrAAZP+ZZs" +
                "xmZgAAAMtWUIKOmo9FT+EAVphOakULSMyZZrWfpebm5unp6Zp8fq+vr7Bjdv8zZsewZPW2bNSuaWYDSa" +
                "CwvnYWdJkzM2d0gLe3tnx0QXtGfIgVe6CdhDctG559TIKUpa4iQqarr768t7ixarSueKCqsszMmdgAT6" +
                "IBS7eHVp4dRRcDGO2cgzABHdQqUK4Aak9WXX4DZmYzM62oe00KVq0ASuS9cYKIjlkHT64OQ7wPTOgTVk" +
                "FNWecKVdIAUcEAUNQXd7UYe30bSLK2uOHh4W0QfoEZgLmxX+Pj4x0EIeoZdPOxcq4MbB4ZD+cHVD1HUd" +
                "Pg6sgWeKoZfOxOXOSxbns4UefQdU5OTre5umxVSLaqacOvuK++zNISTnkYL2YzZqiGUYKEh4SGiMyZmQ" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAAAAAAALAAAAACDAWQAQAj/ABcIHEiwoMGDCBMqXM" +
                "iwocOHECNKnEixosWLGDNq3HgxAIyPGQYs5LFIwAIZAsxQGTggjQMHBBzQIPDgwwcQPXAoUMCDo8+fQI" +
                "MKHUq0qFGMOuYonTPA48enIGEIOLIgCR0TV+loFUKhKwWuFAi0eOngCgWyDkaQQUsAhM2bBAjYtEJgx4" +
                "C7R39mgMoXRogFIUAO7Qs1r+GGhA8rVihgiIABZiY8FnFiiJkBd1LAdEDBBJE6dUyYkED4Ix4JWLXa0H" +
                "rVK4UxrYWMdUDGa8yXt3FbsRlXJo0PdGu+BUGAkY8ECRAUeUIgS48dj0UK3eu3YGAYGQY+FclDcEHpCq" +
                "kX/+7ON7vC0ugLGxSfHqr5g+zbV9cuv3TC+PIXN5xDkL/ApHMIkIAPasj1gYEfkJFHHhQoERpoD0IYoQ" +
                "kQejaaFvXBgAcXomWllWhegQEbayaA0VULLeT2kgqpfShcbV3hRgACyCXgggtANBFGDSsUUAAOIt0F3l" +
                "H4WRDCkUji915BiR00QJMD4Vcfk30NiVhfCEEpUIbo6eeTDgsAuBRTDGgAQAcApFkEDWxm4QFvbM70Fg" +
                "Fx0jnTS2vhRtZtI6gYowMqVHgWTC3SAVZXQqRmgwqMSuAoHnhogUd7fGRwwAEXXFAGCijcsCmnNzQRBK" +
                "cd3IBEFBXhFwBDSlLJ5VN/Ef/05KvqWUdrrFtieZCWA113q5fALpTDmMQWa+wAAiSb7BIBrJBFXCogYS" +
                "YCbn3wAI0JRGDAjQY8Ue0TCNzoAgdo3ITADXqki0IHF9SwUwBLTNFAB4QgWEQHHSDRwRRLJGtsDsHuqm" +
                "vABBeckZSEWUkReubxpbDBXnK5JMQVzfpURg4L/NTEFF+EsHsmueqdQALwhesC7J1M33YafwQefiGjDN" +
                "WQjx1kAV+ytmeeUx/FLBB7q145pUAWfxS0QDy7TFDGBKXctMnSlQyVBQTdDJXPRcNQUM0GWX3x0jPDB9" +
                "XJ5REkNaxPjx01X1QbJV4APRWU9HwLlD3Q2R+d7LRBJif/NLfRA5Fnd6/ubY0zS4eLffVBAmTAcZZjM5" +
                "T10QvMPWRfcTfVd5SRE+QrdmBDFfcCA3j9EeEbG15rrmiv1znrIy+AN90yt4567B3nrvvuvPfuu2GOI/" +
                "7wkw8LFFh4Sx5PUQCPN035dzD0lAEPGaxaPULMyz691sQ7GT3K1Fv/vENNiaeydno3L4AF4nk/+n3vdc" +
                "93BkiGsJdEwS99PsrNkw7D5fvb0vt+l7v8WQQvCkEgAX9CuwXqZwBvQ4gCHZhAOVDwghjMoAZ/MiylAK" +
                "YvAQDPHEJwAgGQYAJGgEJVENWCE6UlNw8YwZ6qxRsC7MYmD6jABDfIwx76MGABCMAJ/4zwhQAYwXEfg4" +
                "GjWLMaD9GhM1cxQVdA1BUCyFBPL+nTnMhCHCt4wEbKiUtzVoCD3lFHZb56DM+k85SQWW18jJOPz37IEF" +
                "7RsSE6ANgCoCAApTzmCGBaAW8+MBM29WZFgQgEhSJEBBuQpj2nSc0YsCJFr5yhkiogi1lQFBa0vHBPFJ" +
                "hNn17ym7jYRA0+iEEc/gQTA9wACI8QgAKA1LvZwaBtsGtjQuz4MxBKhFe2lIpCLOAzXn7ua3erkvcG58" +
                "AxDaBUTdiWCwywhS0IB0Eqyo02caOEriiBQpXsjFco1EghqIALGKLVR7jABUeJRopkEQJr6JAbNhHHk6" +
                "bsgwcEkf+cC/wAAvhiQZo00IAG2KEDDPCJ4NT5lAHWbWA560vM8PMwXv1Nawm8HERDt7rK+fKOFhGTsf" +
                "w4hSnQyy0gaEKaflAtA4HgmnPCIQjqRMMDsakN2NwNXVyDoJuEMkVzIcCgYFIturiBLEXQgAZuAIGmdk" +
                "CgN9BAU3+ApoOyQAMdaAKqIKYlIRVPgkj6akQGwIP68UCsQ7lL/UKwQ5DyTgdwhetI50rXfynkDh4Mk1" +
                "v3yteEzO6rEFSY5spzPseVB4mPMax7kBgSCDLWSvZDopWYh8TRMa801RMsY9+zvuttrUho5dvrGrK+vm" +
                "RgjlF6rNwY6zPKOs4kEHSPQ1liOhj/BOCYq8VsCGWl2I2ptnKsddLf6Ie94MZ2Y7M1DLIua9pepg56YX" +
                "Nu3lr2P4SwRySp89WqTAfdoREtcbZ6lUM2d57RGg+8TOOc7Wo3Xc8V7rwd9d/qspYekVXXdbZjD8ey9j" +
                "Lzfq5/QBEAc8uz24SUzrQBdO1p/Zaw72wWtQ+WoH7/4lgk8paxpK0tSAKAWvghFiKNM22Hb/cRC4hEwa" +
                "0NrtkYC8e5UXizDnbPizFskItKB8VO0jBxD4LjFSMRjgTLX1uFlxDlSS60iBnxd5FMQCGRL7ROPjL8iA" +
                "blIUMkyhexsnUAvEEDym9+ZBWsbZEltsdQ77676t/3EtKdKyUT/8hIE2auxHo6ktn2Phg1SMnYGuIAYu" +
                "cxJWseds7qFIR0x8Q8yN564ofmnJ2WrIp2HZ4fExggG3CZe9nL/iLd1057+tOgtsheCtyQAZTgELLjwW" +
                "WUHOpWu/rVC5jDHeK8sQ5DRjJGIEEJULIARZyFAldwgBuCjRs3zAmbFejBCnqAASbD+tnQJlgH/UiYzM" +
                "7hBAGQgR9MYpUzUAAM4fwpZwaVSbagZSY1/QAaKiAJSSQ3g16Nt5Ovw2WMyFvLn+ZlqEXqx4+hpkVN1M" +
                "qIwtkCFXiFk0MlQ0x+84CjvgSnp4wAgh6ggAyeMbynjS7R9Ovsiz4Fl67Wd6tv7Zgg5tqIA/+YgyNCM5" +
                "rSaOHfJCqUCZpogjOgCEWVTJG5YXLDA5GSBsSRpgGKYCA27PMFYQgABpYugHZXQTEXdy/oWCeSY4JXgq" +
                "ZtcHlxNljTFi+JfCG1X8F+S40ezuNyZgzY4fzAuyQrBH8QQbKQNYAcKMCQKwKlg2zQFUemkzCnsUGLBn" +
                "4oFokziwZH0W1oQBeyuIEADvdNW4JKFmvZ8ANqQA4CDNAELDShAM3eSNRR9x6Wybe5CvlbyP7GdsxGJ8" +
                "R23AsxFWb1qUtdxHdJtPk4atress0gVn8efRtdsGHpMQRgIp1bDITNOF0BEICgAGgWGaHph+aR7Xn5GM" +
                "BwBhL9lIrhTtH/T0cgBBPZhi1teMuBUlNJAhwnBir6zQci4Gzl8mr4xdN3MBFS2gwVRFURoW+8Qnb1tU" +
                "DTtgMGcAE3kgBPwAYHsiCDok1e4BpSNH1TtAdTVCF10EiOpE7uZAJjMCJRpAI3NxYEIHN54gbsFxbqh0" +
                "MxoX4gUAQGAAQ0CAQoQFAG1QFiUH8SgXb1AUf6h3qnZ14PdXWEwWrUVWeiZYQbxVdy5UxLkC8agAUxBR" +
                "x5AhNbhBtZuBlcyIVC1SAm0AIT+BkW2BVnMH2LwijoNCm0wk6NQkUq8ADyZy43gAJl0AFmkiYd8E8QQF" +
                "VPBQBYJQk8CDl88W4e1YSEMTrIIiUgVyTA/xd71WYlZ0Y7AtiEUhJAkxhAPTRtY1IFT6UBWQAcWJAmAG" +
                "AAb4EApIgAc3FN9jRTNPAALUUWsMgmWNSFuaEC82QCkecA8lRzV2RwXmFwLyFOXREHo/IDddgpYXAD+X" +
                "IAYcAETMAHT3EADXABBdUBT9eDQphAhEE56qRkPrgqIndccsR7Smhf5xhjXIKEe4VXeFVXS3EEyYKABh" +
                "AGBsABHNAI5TJ5H5AGj3BVTzAcBCAIPgAcBkIXc6JT2aR+DSdswnEgdgJ0CEIc6pdUdmAHaXJQAMACBX" +
                "UDhQABl7IEKdcxIhdtJmkUcRVXAvGO8DhXsmQXzqQsKdeSNNmSdwBXvP9TkifpQ8iCJFzjEwfGTDs5lA" +
                "aRNUchlHekJEmSXhVhOkCocURZhPJRbxRklBjBlK9mS3CDZAQIHu3BVvJxYu/1XWM5hEuSIUOSNfHBWT" +
                "/4EOS1EP5nX1ayN9KVH+HlXbmEHtIxfExDl3Wpl//HJYdBju0xEHOjZ+DllyujNIj5XGfzOWQJOE4SAp" +
                "TVVVeHOBuzVpR5mYRol+x1S11Tlljpl4qZNIwmOt2VjixBmb6HTLn0MHR5mKrjmqVZlkMhdorzNbJpNo" +
                "lJhFJJfJi5YaYnHnjjMwvVaFYZmaopEMepEW9pXVMTms/1moG5Xor5X8kElUOYZ8eZlkxoetXZXof/6J" +
                "p2tjrXaZvT8SpzWR/ruV4khpUWpUzh6ZnbyRgZQpW/qYnoaB8FcZzpoTf+ZZsEeI4DmmfwpXWK2ZXziU" +
                "boGZUO+qAQShSXZmBsRaGDCZyeg58RChET2leeBVKaJhA3w39S0Vaag2VPU6Elo2S2Z11sVzcjpmjdYR" +
                "4lI2Yhsxc32mLmMaN2NogFFhjJ1TNx9jA8EDKOhRCBETICJjaxMqLeEzR7NhHYESQtmja3hVpJypwNRB" +
                "DdMYgE5GUY+mWPiDwEwWkQ0Wapx2XME0T+06W75KZBZAFwtqar8iRuGhHsoz9Zojfj8yQxY2Q5w45gWl" +
                "Ev2hB5ujQd1qGIU0wB//ShG+oQioo/+immr7aljwo9ClMyl7qpnNqpnvqpoPpqhRqqpFqqjIObpMUdFk" +
                "Q6ptqqropnY9YQciB3spMsMgAeA7ATAzADBLACPKAAPaAIrzqsl6pHC7VgCSEDJWAGMlAZMkAVC6AZXk" +
                "gWrziRG6AAXkqs2gpSSjFrhOkXaWkBJ+QEATABJ6BCCoAi5ZYbxIZFhnR5p1QEFaAAobet9tppIvWtf7" +
                "YAeCUA5OoEX8Bt3yZFUqSuO7cZbMIb6XdsbvEAKVBG97qpmxWpfeVMHkdMAbAGX9ATVoEVk4QoJ1JwnN" +
                "SuXeiuDGdDaiAI44IIZ+VDSfQX9HabpTGqe6WTff/FkkxhP6VhCAPQsR4CG5UUtD9lgjdXi7TRcw/QUw" +
                "QQAeJyARVASw7kVdQRQvPmHVqGog3hn6gJazbraTNpsQijBZIUc1FEga5BASR4cyrSro83JzREAI2QKT" +
                "4iCQ40egc6OFqLoUnYhKDWtZ8WCUbkBEYQAH7wBYXbFNjXFxJgAyMScE6kGjylST9FAXkCdFdEE8fWc3" +
                "SRBQjQBGLAYUvXsgVjtx9keyxDHo2FLLgDnTpzZPHGEgOGrAYmAJFlWhxWfwMQAuzjHhy2t7ArHvTjbL" +
                "q3WD9JMQKwCCgRAF9AArY6BJAQIJ1RIS1XGqjxuHQgeB8STsDYGVcoFi0kh4z/l03C0QVZ0HMGGRcecB" +
                "wIQCPiUgCHQboxS3Wly5+pJ1GsB5eYE5a5iZbhwSVkk7/tYYgFmq1BEUI8QAUCcKtNAWhMcVS3ERNgCC" +
                "EdiB7uRCLZyxpjMLTtRxa1MRsTd4UOkLRzaC0PGRc9FxcI8AIucAOPQLdAAb8jM5wg8XoUq5wllpdr1p" +
                "m0Mj5riUTokai/spi0AsQEdiRSQrNHMSyygywB8gd9tACCVEh38kkrkoEsl7h9cRrVSwcf23dbcXPbSw" +
                "FXBBOzoXCexIXVyhY0RBdFUCMu8AJFoAIEkAI10GwEDFyySzKOczSSlaEbg6r7GTglqXVleoTfsRD7t5" +
                "9p/8eblGha7zN8HNMXIPeeixwwSRFCslY5/qEAg3QghCTCNAATcAAHi0R9pWwDf4cekCJ44dQhrTEbhV" +
                "eCnKFNITtKM6R+VxBuFNDGN8Kr8kdxvKNh7zNcu8S34zmds3sku7tRxAwRvDR8cmPMv1mlBDMHehRrA6" +
                "EDOPAsvHFDCHIFb/AG0ld91BcaZGghWKzKjuJtXTFw4RZu8vRE4oQiVExKLJKLLzJUewIuBoABgggxWo" +
                "lfi+O7y3SZtVeY5pjD42XMvMJQ8UUx1xxrSrEDN4IAHsAG8EoAEKgnGKjLpXzOGjhzbFgfkiIBIUhJri" +
                "xwVIS2nnTPJEIGv6GCrhGHH/+gT8eRHJhrEx7wAplyA6TYAXnBl/y7hA+9neeoYSBhJI/R0PLplgy9UQ" +
                "5Nnr/DkgJQANziAgngARHgAxSAQ23wEsN2sCowgWK8IhoYGt50SRNMK5CyzgQHRSRCbG7QfR9CJ+pHF1" +
                "cYvjHgxiuMhwBwUB1wkVKQUEAR1VI9zQZayL93t5IZyGkDFUiMwzqcjhTlaceSCJ1STaHoybzBFluITX" +
                "uSFj9FAF4wgaU8TqBRTuekBanMJezUTiZtAkLgSQ9QScPYU+YiVVQlUICIhxCQkQfFETo2sYxFGJOM2P" +
                "sLnpVN1OQ5fMedTP/LmcgdUXyBhBCknwQ0UkfQAQcAAXr/wAExQJARiSCNp4U3IYfC5sChjUW32CAWKI" +
                "ZCcM5By36jYQKMwiiS4tpcwChwewGcEgRoMlAd8Cm+3VR9WCoXULwSkcgGZsgJPdSMXWJnlbsIU5Tp4c" +
                "OjVYmXKdTVcyQDJp4/dCwCwAB/qAFpYBNFkCadAr4gwAHI8QK7IcUE8NVdKBwPwMEgTAa/uAcYCGwrko" +
                "v6HM+yPUUtor2o4UiWcgDRyAR9AY2X8ggXUIdBcAGP8Ck3QBH4xxC1Z3av0mJTqW8+2BfRXdT6dtD/ia" +
                "/Goiw7EBcXQIpFMBePkCYsUC6c/VKG1AaGpJB10oLbhBt4TgNhrCIwUUVgHegOh1Ou/9HmS2Xgf2gHo4" +
                "ICQWAmTTAvGcAEHdAAhB0RCvZaDrHpjpoYsPfHaBXqT1FgxI3ISL1hczmx1sXqCsEDqf5x7OhWODtXR6" +
                "AANTADHSAFGvADEeAB5HvRFMnZH3Aub8wIc/KQNkUDENfNBul4PRVDwgbaHIxNY5wGTcACfwgADWAmds" +
                "BUEFAIATUvdrAEJCnNEdtXT0iT8jgHR3AETCFLFVABWVATT9AEGmAAdP4BT4AmEDAIEaCQ3rzXyXFDdG" +
                "EjFb0bL/UWdIFNBIAcLtAE++6PWIWR2t4ALKDtIfAYY3Lu0p3uJglXnFiTc4UsDLADm/0BjbDr+RLgPz" +
                "ADKfAsYf+wLyZVLwRQAYF9kR3QRy65AzDZkhFtMH4L8huqA7VO8kif9PCYA8nnO6dO9K/2uobxjk0PV+" +
                "8Y9AiBk1C/9TnGMHCjEcE0dWt1x3wlMSCVnBYx9tSloRg0wBgxO0ZCmUGDlUNp9neE9hVB97/J9lGLlA" +
                "swiYCFtQKNoXqfVl4VFIcvpZB9b4LPEo1PFI9flAiE97KS+MUMnlge+a6rGMeE3TXWHlxOGGAJ+p/5PO" +
                "wxR+lVH3NklNgpyOkR2c9pYEkUEszdniAONKneYWFOnqofnID5mecT5gqD+6Ux6xrB4bOHpEWsYal1WK" +
                "/lWM7vONwBXlYHHp/zv8HrdvEBckL/bbVhd1akrrfMTdwWpl6ZqbO2OZqjVaBYY7usyYTZv4iLDf2L1c" +
                "fAf3sbprsbxf5BDRAwBA4kSDDDgAUJBRREmDBhBoMOF0AcGEKiQ4YXEwYgaHHBgIICLWAcmEHjyY8FJY" +
                "Ls2DBhQ5UXF5ZEqTHkTYEOORK0STCAxIwSKQr0+LDjxaEwTJIcOHJlzJoOWQ68GBTpUYVWjdJ0mLToVo" +
                "FLo44le3LATpwCf06MeHGqQJdeT2oFynOBBapDeSyYKXDvRbRpYTy1qzGE4JtlFxTMEMLx48dMiRqGup" +
                "igS7AwvsqVeJjrAs9U3VbeiDhn3YGYu2Jl+1mqVc4OQytVXNv2/0q8MZOKfX15dcW5vk/21cxjoICUYS" +
                "2LvOpTwADold8OPjm7MWTsjhUX/IqSO+XCy+E2n/y7vOy2oEknF50ZRoDn0cOLh6E6c9HdGqfHZZ3Q+m" +
                "0AVwvAPvNo44uu/ArsLiG6hLpJrMDm+4+w9tg7Tb+CkKuJwOCAGyvCngZai0GfyNPMQQ/R+2y67qZTUT" +
                "kKLyQxtZNiI64+E3mLzb/0ArQtqbAyEDIk3mZTKoAQcptvxwI1G4CHAWMc6K8ZCdLQIeMIsoCHJyOk7q" +
                "X1ejPIAscCGIq3qL4jC8jr2LxISdM2608914xUasiQsCxoyy7zRLGiLhva0cgMkIRTxtbO4//RNR8Vsx" +
                "MnNHVCzD4mm6RRogid0rNHMRm7UcqxBgDywe3m3HBUgwj0kqgdmZywU4OMlLKkT00UrtJVhfvzxM44bR" +
                "RA6IK9LVgOfSS22F/1gy7Zsohl1lhhFYv22WaXtW3a2o6F1lpqu/X2W3DDFXdccss191x001VMgCvVpT" +
                "YEZAdY0F166+WrXXvRHQDffL8VslkcUTrMXTPHaqxfhNX9N2FzQUKWYR+FRItfCwN+EdGHChUxzUhl+h" +
                "KlADRFStP3wOR3oYYW0qnjDEheCySKxypoRJvMPA4l4g6qabZ5Jb4ZJR6GovnaM2sSUlReASsp6Rpjhv" +
                "jXhRcwjkCHd/7/WKOoJ+r4pXgtHu5qiVBewGaECq7xJ5BaZpBisckee2vbmAbqq5LHGniuoWsUa+qTLO" +
                "DNbNtU1pPArGueu6aQnwY36qoxLPZulLKGHMAMnE4J2UJB4sskGGJW2QLkOg8B7swHE4Bzy1FKO4Tn5M" +
                "ZoM5bfg5cjs7yOXKzGqxJy98LLKtx1rTf0GvgDFV8cd9vBpJKyqLKuuzqnLci7qtSNy+Cv3Ztf2Hqnrc" +
                "e+d4cEmDf4170rCmbHJb19TeRVBRvn8cXOinC4yT9w3tyNpzaAEUXlMOjxgIdlLRMJ5qa3ubFIryxIks" +
                "r1ouI/2YjsJAx8iQNBBredWEBUMbuTWk6lTBTWwQ2ASlmeTvqnM7OgZUsXjMpODgayA0pFfHsBXuf0d0" +
                "Pf1U9ayQubDnHYLeL9sF9n4dLpOqZAISZRiUtkYhOd+EQoRlGKUzReQAAAOw==";

        Thread.sleep(5000);

        LogEvent[] getLogsInfo = logViewerClient.getAllSystemLogs();
        boolean assertValue = false;
        for (LogEvent event : getLogsInfo) {
            if (event.getMessage().contains(gifReceivedContent)) {
                assertValue = true;
                break;
            }
        }

        Assert.assertTrue(assertValue,
                "File not uploaded and received to ESB");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = {"wso2.esb"}, description = "Pure Text (Binary) and POX Message Support with JMS " +
            "send simple payload to as POX")
    public void testJMSwithPOXM() throws Exception {

        JMSQueueMessageProducer sender = new JMSQueueMessageProducer(getJMSBrokerConfiguration());
        String queueName = "JMSPoxProxy";
        try {
            sender.connect(queueName);
            for (int i = 0; i < 3; i++) {
                sender.pushMessage("<m:placeOrder xmlns:m=\"http://services.samples\">\n" +
                        "    <m:order>\n" +
                        "        <m:price>172.39703010684752</m:price>\n" +
                        "        <m:quantity>19211</m:quantity>\n" +
                        "        <m:symbol>MSFT</m:symbol>\n" +
                        "    </m:order>\n" +
                        "</m:placeOrder>");
            }
        } finally {
            sender.disconnect();
        }

        Thread.sleep(10000);

        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(getJMSBrokerConfiguration());
        try {
            consumer.connect(queueName);
            for (int i = 0; i < 3; i++) {
                if (consumer.popMessage() != null) {
                    Assert.fail("POX message not delivered to the Queue");
                }
            }
        } finally {
            consumer.disconnect();
        }
    }

}
