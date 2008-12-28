/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sdb.layout2.index;

import static com.hp.hpl.jena.sdb.sql.SQLUtils.sqlStr;

import java.sql.SQLException;
import com.hp.hpl.jena.sdb.layout2.TableDescNodes;
import com.hp.hpl.jena.sdb.layout2.TableDescQuads;
import com.hp.hpl.jena.sdb.layout2.TableDescTriples;
import com.hp.hpl.jena.sdb.layout2.hash.FmtLayout2HashDerby;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.sql.SDBExceptionSQL;

public class FmtLayout2IndexDerby extends FmtLayout2HashDerby
{
    public FmtLayout2IndexDerby(SDBConnection connection)
    { 
        super(connection) ;
    }

    @Override
    protected void formatTableTriples()
    {
        // TODO Generalize : return a template
        TableDescTriples desc = new TableDescTriples() ;
        dropTable(desc.getTableName()) ;
        try { 
            String x = sqlStr(
                              "CREATE TABLE %s (",
                              "    %2$s int NOT NULL,",
                              "    %3$s int NOT NULL,",
                              "    %4$s int NOT NULL,",
                              "    PRIMARY KEY (%2$s, %3$s, %4$s)",
                              ")") ;
            x = String.format(x, desc.getTableName(),
                              desc.getSubjectColName(),
                              desc.getPredicateColName(),
                              desc.getObjectColName()) ;
            
            connection().exec(sqlStr(
                                 "CREATE TABLE "+desc.getTableName()+" (",
                                 "    s int NOT NULL,",
                                 "    p int NOT NULL,",
                                 "    o int NOT NULL,",
                                 "    PRIMARY KEY (s, p, o)",
                                 ")"                
                    )) ;
        } catch (SQLException ex)
        { throw new SDBExceptionSQL("SQLException formatting table '"+TableDescTriples.name()+"'",ex) ; }
    }
    
    @Override
    protected void formatTableQuads()
    {
        TableDescQuads desc = new TableDescQuads() ;
        dropTable(desc.getTableName()) ;
        try { 
            String x = sqlStr(
                              "CREATE TABLE %s (",
                              "    %2$s int NOT NULL,",
                              "    %3$s int NOT NULL,",
                              "    %4$s int NOT NULL,",
                              "    %5$s int NOT NULL,",
                              "    PRIMARY KEY (%2$s, %3$s, %4$s, %5$s)",
                              ")") ;
            x = String.format(x, desc.getTableName(),
            		          desc.getGraphColName(),
                              desc.getSubjectColName(),
                              desc.getPredicateColName(),
                              desc.getObjectColName()) ;
            
            connection().exec(sqlStr(
                                 "CREATE TABLE "+desc.getTableName()+" (",
                                 "    g int NOT NULL,",
                                 "    s int NOT NULL,",
                                 "    p int NOT NULL,",
                                 "    o int NOT NULL,",
                                 "    PRIMARY KEY (g, s, p, o)",
                                 ")"                
                    )) ;
        } catch (SQLException ex)
        { throw new SDBExceptionSQL("SQLException formatting table '"+TableDescTriples.name()+"'",ex) ; }
    }
    
    @Override
    protected void formatTableNodes()
    {
        dropTable(TableDescNodes.name()) ;
        try { 
            connection().exec(sqlStr ("CREATE TABLE "+TableDescNodes.name()+" (",
                                       "   id int generated always as identity ,",
                                       "   hash BIGINT NOT NULL ,",
                                       "   lex CLOB NOT NULL ,",
                                       "   lang LONG VARCHAR NOT NULL ,",
                                       "   datatype varchar("+TableDescNodes.DatatypeUriLength+") NOT NULL ,",
                                       "   type integer NOT NULL ,",
                                       "   PRIMARY KEY (id)",
                                       ")"
                    )) ;
            connection().exec("CREATE UNIQUE INDEX Hash ON " + TableDescNodes.name() + " (hash)");
        } catch (SQLException ex)
        {
            throw new SDBExceptionSQL("SQLException formatting table '"+TableDescNodes.name()+"'",ex) ;
        }
    }
}

/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */