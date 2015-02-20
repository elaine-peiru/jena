/**
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 */

package org.seaborne.dboe.trans.bplustree.soak;

import java.util.List ;

import arq.cmd.CmdException ;
import arq.cmdline.CmdGeneral ;

import com.hp.hpl.jena.sparql.util.Utils ;

import org.apache.jena.atlas.lib.RandomLib ;
import org.apache.jena.atlas.logging.LogCtl ;

public abstract class BaseSoakTest extends CmdGeneral {
    static { LogCtl.setLog4j() ; }

    protected final int MinOrder = 2 ;
    protected final int MinSize  = 1 ;
    protected int       MaxOrder = -1 ;
    protected int       MaxSize  = -1 ;
    protected int       NumTest  = -1 ;
    
    protected BaseSoakTest(String[] argv) {
        super(argv) ;
    }

    protected abstract void before() ;
    protected abstract void after() ;

    @Override
    protected String getSummary() {
        return "Usage: "+Utils.className(this)+" maxOrder maxSize NumTests" ;
    }

    @Override
    protected void processModulesAndArgs() {
        List<String> args = super.getPositional() ;
        if ( args.size() != 3 )
            throw new CmdException("Usage: maxOrder maxSize NumTests") ;
        
        try { MaxOrder = Integer.parseInt(args.get(0)) ; }
        catch (NumberFormatException ex)
        { throw new CmdException("Bad number for MaxOrder") ; }

        try { MaxSize = Integer.parseInt(args.get(1)) ; }
        catch (NumberFormatException ex)
        { throw new CmdException("Bad number for MaxSize") ; }

        try { NumTest = Integer.parseInt(args.get(2)) ; }
        catch (NumberFormatException ex)
        { throw new CmdException("Bad number for NumTest") ; }
    }

    @Override
    protected void exec() {
        int successes   = 0 ;
        int failures    = 0 ;

        int[] orders = null ;
        int[] sizes =  null ; 
        
        if ( false ) {
            // Specific test case.
            orders = new int[]{2} ;
            sizes =  new int[]{20} ;
            NumTest = sizes.length ;
        } else {
            orders = new int[NumTest] ;
            sizes =  new int[NumTest] ;
            for ( int i = 0 ; i < orders.length ; i++ ) {
                int order = ( MinOrder == MaxOrder ) ? MinOrder : MinOrder + RandomLib.random.nextInt(MaxOrder-MinOrder) ;
                int size =  ( MinSize  == MaxSize  ) ? MinSize :  MinSize  + RandomLib.random.nextInt(MaxSize-MinSize) ;
                orders[i] = order ;
                sizes[i] = size ;
            }
        }
        
        boolean debug = false ;
        // Number of dots.
        int numOnLine = 50 ;
        int testsPerTick ;
        if ( NumTest < 20 )
            testsPerTick = 5 ;
        else if ( NumTest < 200 )
            testsPerTick = 50 ;
        else 
            testsPerTick = 500 ;
        
        
        // ---- Format for line counter.
        int numLines = (int)Math.ceil( ((double)NumTest) / (testsPerTick * numOnLine) ) ;
        // Start of last line.
        int z = (numLines-1)*(testsPerTick * numOnLine) ;
        int digits = 1 ;
        if ( z > 0 )
            digits = 1+(int)Math.floor(Math.log10(z));
        String format = "[%"+digits+"d] " ;

        System.out.printf("TEST : %,d tests : Max Order=%d  Max Items=%,d [tests per tick=%d]\n", NumTest, MaxOrder, MaxSize, testsPerTick) ;
        
        before() ;
        
        int testCount = 1 ;
        
        for ( testCount = 1 ; testCount <= orders.length ; testCount++ ) {
            if ( testCount % testsPerTick == 0 )
                System.out.print(".") ;
            if ( testCount % (testsPerTick * numOnLine) == 0 )
                System.out.println("") ;
            if ( testCount % (testsPerTick * numOnLine) == 1 )
                System.out.printf(format, testCount-1) ;

            int idx = testCount - 1 ;
            int order = orders[idx] ;
            int size = sizes[idx] ;
            try {
                //System.out.printf("TEST : %,d : Order=%-2d : Size=%d\n", testCount, order, size) ;
                runOneTest(order, size, debug) ;
                successes++ ;
            }
            catch (AssertionError | RuntimeException ex) {
                System.err.printf("-- Fail: (order=%d, size=%d)\n", order, size) ;
                ex.printStackTrace(System.err) ;
                System.err.printf("--------------------------\n") ;
                failures++ ;
            }
        }
        
        // Did the last loop print a new line?
        if ( (testCount-1) % (testsPerTick*numOnLine) != 0 )
            System.out.println();
            
        after() ;
        System.err.flush() ;
        System.out.flush() ;
        System.out.printf("DONE : %,d tests : Success=%,d, Failures=%,d\n", NumTest, successes, failures);
    }

    protected abstract void runOneTest(int order, int size, int KeySize, int ValueSize, boolean debug) ;


    protected abstract void runOneTest(int order, int size, boolean debug) ;

    @Override
    protected String getCommandName() { return Utils.className(this) ; } 
}

