package org.molgenis.blood;

import htsjdk.tribble.readers.TabixReader;
import net.sf.samtools.util.BlockCompressedInputStream;
import org.molgenis.genotype.Allele;
import org.molgenis.vcf.VcfReader;
import org.molgenis.vcf.VcfRecord;
import org.molgenis.vcf.VcfSample;
import org.molgenis.vcf.meta.VcfMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.molgenis.blood.GenomeBuild.b37;

public class Main {

    public static void main(String[] args) throws Exception {

        if(args.length != 4)
        {
            System.out.println("Please supply:");
            System.out.println("- File location of reference data ('Montemayor-Garcia_etal_2018_SupplTable1_fix.txt')");
            System.out.println("- If you use genome build 37 or 38 (either 'b37' or 'b38')");
            System.out.println("- File location of a GZipped VCF to be analyzed (ending in '.vcf.gz', must be accompanied by index file with same file name ending in '.vcf.gz.tbi')");
            System.out.println("- Output file location. May not exist yet.");
            System.exit(0);
        }

        File referenceData = new File(args[0]);
        if(!referenceData.exists())
        {
            System.out.println("Input reference file not found at " + referenceData.getAbsolutePath() + ". Suggesting to supply 'Montemayor-Garcia_etal_2018_SupplTable1_fix.txt' (located in blood-group-variants/data/).");
            System.exit(0);
        }

        String gbS = args[1];
        if(!gbS.equals("b37") && !gbS.equals("b38"))
        {
            System.out.println("Genome build must be either 'b37' or 'b38' instead of "+ gbS+".");
            System.exit(0);
        }
        GenomeBuild gb = GenomeBuild.valueOf(gbS);

        File GZVCFInput = new File(args[2]);
        if(!GZVCFInput.getName().endsWith(".vcf.gz"))
        {
            System.out.println("Input GZipped VCF file name '" + GZVCFInput.getName() + "' does not end in '.vcf.gz'. Are you sure this is a valid input?");
            System.exit(0);
        }
        if(!GZVCFInput.exists())
        {
            System.out.println("Input GZipped VCF file not found at " + GZVCFInput.getAbsolutePath()+".");
            System.exit(0);
        }

        File output = new File(args[3]);
        if(output.exists())
        {
            System.out.println("Output file already exists at " + output.getAbsolutePath()+". Please delete it first, or supply a different output file name.");
            System.exit(0);
        }

        System.out.println("Arguments OK. Starting...");
        long start = System.nanoTime();

        BloodGroupTool bgt = new BloodGroupTool(referenceData, gb, GZVCFInput, output);
        bgt.run();

        System.out.println("...completed in " + ((System.nanoTime()-start)/1000000)+"ms.");


    }


}
