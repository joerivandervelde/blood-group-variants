package org.molgenis.blood;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BloodVariantDataLoader {

    private File src;
    private List<BloodVariantData> bvdList;

    /**
     * Constructor
     * @param src
     */
    public BloodVariantDataLoader(File src)
    {
        this.src = src;
        bvdList = new ArrayList<BloodVariantData>();
    }

    /**
     * Return data list
     * @return
     */
    public List<BloodVariantData> retrieve()
    {
        return bvdList;
    }

    /**
     * Load data from file source into list
     * @throws FileNotFoundException
     */
    public void load() throws FileNotFoundException {
        Scanner s = new Scanner(this.src);
        String line;
        while(s.hasNextLine())
        {
            line = s.nextLine();
            // skip header
            if(line.startsWith("System_nr"))
            {
                continue;
            }
            String[] split = line.split("\t", -1);
            BloodVariantData bvd = new BloodVariantData();
            bvd.System_nr = split[0];
            bvd.System_name = split[1];
            bvd.Gene = split[2];
            bvd.UCSC_Transcript = split[3];
            bvd.Nucleotide_change_UCSC_tr = split[4];
            bvd.AA_change = split[5];
            bvd.Assoc_alleles = split[6];
            bvd.Assoc_antigen_or_pheno = split[7];
            bvd.hg19_chrom = split[8];
            bvd.hg19_pos = split[9];
            bvd.b38_chrom = split[10];
            bvd.b38_pos = split[11];
            bvd.Read_depth_1000G = split[12];
            bvd.dbsnp_or_dbvar_id = split[13];
            bvd.Interpr_ref = split[14];
            bvd.Interpr_alt = split[15];
            bvd.SP_EA = split[16];
            bvd.SP_AM = split[17];
            bvd.SP_AF = split[18];
            bvd.SP_EU = split[19];
            bvd.SP_SA = split[20];
            bvd.Pred_Poly2 = split[21];
            bvd.Pred_SIFT = split[22];
            bvd.Pred_MT = split[23];
            bvd.Pred_MA = split[24];
            bvd.Pred_conseq = split[25];
            bvd.Ref = split[26];
            bvd.Alt = split[27];
            bvd.tabix_b37 = split[28];
            bvd.tabix_b38 = split[29];
            bvdList.add(bvd);
        }
        this.bvdList = bvdList;
    }
}
