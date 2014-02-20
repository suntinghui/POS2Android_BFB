package com.bfb.pos.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class BankParse {

    private static final String SPLIT_REGEX = ",";

    private Context mContext;

    private int bank_id;


    private List<Bank> banks;

    private BankParse() {
    }

    public static BankParse build(Context mContext, int bank_id) {
        final BankParse parse = new BankParse();
        parse.mContext = mContext;
        parse.bank_id = bank_id;
        parse.parse();
        return parse;
    }

    /**
     * parse from file
     */
    private void parse() {
        try {
            parseBank();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseBank() throws IOException {
        final List<String> strings = readLine(mContext, bank_id);
        banks = new ArrayList<Bank>();
        Bank bank = null;
        String[] splitstr = null;
        for (String str : strings) {
            splitstr = splitLine(str, SPLIT_REGEX);
            if (splitstr.length == 2) {
            	bank = new Bank(splitstr[0], Integer.parseInt(splitstr[1]));
                banks.add(bank);
            }

        }
    }


    /**
     * Getter of banks
     * 
     * @return the banks
     */
    public List<Bank> getBanks() {
        return banks;
    }

    private static String[] splitLine(String str, String regex) {
        return str.split(regex);
    }

    /**
     * read file by read line
     * 
     * @param mContext
     * @param id
     * @return
     * @throws IOException
     */
    private static List<String> readLine(Context mContext, int id) throws IOException {
        final InputStream in = mContext.getResources().openRawResource(id);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        final List<String> strings = new ArrayList<String>();
        String line = null;
        while (null != (line = reader.readLine())) {
            strings.add(line);
        }
        reader.close();
        return strings;
    }

}
