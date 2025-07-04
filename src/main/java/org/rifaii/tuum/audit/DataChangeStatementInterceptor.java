package org.rifaii.tuum.audit;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.util.Set;

@Intercepts(
    {
        @Signature(
            type = Executor.class,
            method = "update",
            args = { MappedStatement.class, Object.class }
        )
    }
)
public class DataChangeStatementInterceptor implements Interceptor {

    private static final Set<SqlCommandType> RELEVANT_COMMANDS = Set.of(SqlCommandType.INSERT, SqlCommandType.UPDATE);

    private final DataChangeNotifier dataChangeNotifier;

    public DataChangeStatementInterceptor(DataChangeNotifier dataChangeNotifier) {
        this.dataChangeNotifier = dataChangeNotifier;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object ret = invocation.proceed();
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        if (RELEVANT_COMMANDS.contains(sqlCommandType)) {
            String executedSqlMethodName = mappedStatement.getId();
            Object parameter = invocation.getArgs()[1];

            DataChangeLog dataChangeLog = new DataChangeLog(sqlCommandType.name(), executedSqlMethodName, parameter.toString());
            dataChangeNotifier.notify(dataChangeLog);
        }
        return ret;
    }
}
