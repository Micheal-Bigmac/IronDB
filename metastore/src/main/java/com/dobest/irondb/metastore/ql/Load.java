package com.dobest.irondb.metastore.ql;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

public class Load implements Statement{
    private Table table;
    private String path;

    @Override
    public void accept(StatementVisitor statementVisitor) {

    }
}
