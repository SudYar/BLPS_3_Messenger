package sudyar.blps.config;


import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.transaction.SystemException;

@Configuration
public class TransactionConfig {

    @Bean
    public bitronix.tm.Configuration transactionManagerServices() {
        bitronix.tm.Configuration configuration = TransactionManagerServices.getConfiguration();
        configuration.setServerId("1");
        return configuration;
    }
    @Bean(name = "bitronixTransactionManager")
    public BitronixTransactionManager transactionManager(bitronix.tm.Configuration _c){
        BitronixTransactionManager trans = TransactionManagerServices.getTransactionManager();
        try {
            trans.setTransactionTimeout(60);
        } catch (SystemException e) {
            e.printStackTrace();
        }
        return trans;

    }

//    @Bean
//    public UserTransaction bitUserTransaction(){
//        if (bitronixTransactionManager == null){
//            BitronixTransactionManager bitronixTransactionManager = TransactionManagerServices.getTransactionManager();
//            bitronixTransactionManager.setTransactionTimeout(300);
//        }
//        return new UserTransaction(bitronixTransactionManager);
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(UserTransaction userTransaction, TransactionManager transactionManager){
//        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
//        jpaTransactionManager.setTransactionManager();
//        transactionManager.setDefaultTimeout(300);
//        return transactionManager;
//    }

}
