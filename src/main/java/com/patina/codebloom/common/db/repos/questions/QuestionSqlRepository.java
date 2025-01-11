package com.patina.codebloom.common.db.repos.questions;

import java.sql.Connection;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.Question;

public class QuestionSqlRepository implements QuestionRepository {

    DbConnection dbConnection;
    Connection conn;

    public QuestionSqlRepository(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.conn = dbConnection.getConn();
    }

    @Override
    public Question createQuestion(Question question) {
        String sql = "";
    }
}
