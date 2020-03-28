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

        if(args.length != 3)
        {
            System.out.println("Please supply:");
            System.out.println("- File location of " +
                    "'Montemayor-Garcia_etal_2018_SupplTable1_fix.txt'");
            System.out.println("- If you use genome build 37 or 38 ('b37' or " +
                    "'b38')");
            System.out.println(" File location of GZipped VCF to be analyzed");
        }

        // TODO argument parsing, not hardcoded!


        GenomeBuild gb = b37;

        File bvdData = new File("/Users/joeri/github/blood-group-variants" +
                "/data/Montemayor-Garcia_etal_2018_SupplTable1_fix.txt");

        String path = "/Users/joeri/Projects/GAVIN_Plus/1000G_diag_FDR/exome" +
                "/ALL.chr1to22plusXYMT.phase3_shapeit2_mvncall_integrated_v5.20130502.genotypes.snpEffNoIntergenicNoIntronic.exac.gonl.cadd.vcf.gz";
        File vcf = new File(path);


        Map<String, Set<String>> sampleToAntigens = new HashMap<String, Set<String>>();
        List<String> sampleNames = new ArrayList<String>();

        BloodVariantDataLoader bvdl = new BloodVariantDataLoader(bvdData);
        bvdl.load();
        List<BloodVariantData> bvd = bvdl.retrieve();

        BlockCompressedInputStream is = new BlockCompressedInputStream(vcf);
        VcfReader r = new VcfReader(is);
        VcfMeta vm = r.getVcfMeta();

        // init output
        for(String sample: vm.getSampleNames()){
            sampleToAntigens.put(sample, new HashSet<String>());
            sampleNames.add(sample);
        }

        TabixReader t = new TabixReader(path);

        for(BloodVariantData b : bvd)
        {
            String pos = gb == b37 ? b.hg19_pos : b.b38_pos;
            String chr = gb == b37 ? b.hg19_chrom : b.b38_chrom;
            String chrPos = chr + ":" + pos;
            String tabixQuery = chrPos + "-" + pos;

            TabixReader.Iterator tri = t.query(b.tabix_b37);
            String nextVcfLine;
            VcfRecord vr = null;
            while((nextVcfLine = tri.next()) != null)
            {
                String[] split = nextVcfLine.split("\t", -1);
                if(!split[1].equals(pos))
                {
                    continue;
                }
                if(!split[3].equals(b.Ref))
                {
                    throw new Exception("Ref allele did not match for: " + chrPos);
                    // or, we can ignore and move on..
                }
                vr = new VcfRecord(vm, nextVcfLine.split("\t", -1));
            }

            if(vr == null)
            {
                System.out.println("Variant not present: " + chrPos);
                continue;
            }

            // not necessary since VCF-IO gives us the alleles as a string
            // however - checking here saves time if allele is not present!
            // TODO test
            boolean altFound = false;
            for(int i = 0; i < vr.getAlternateAlleles().size(); i ++)
            {
                if(b.Alt.equalsIgnoreCase(vr.getAlternateAlleles().get(i).getAlleleAsString()))
                {
                    altFound = true;
                    break;
                }
            }
            if(!altFound)
            {
                System.out.println("Blood variant alt allele not in data: " + chrPos);
                continue;
            }

            //TODO test if sample name order is guarenteed
            Iterator<VcfSample> vi = vr.getSamples().iterator();
            VcfSample nextSample;
            int idx = 0;
            while(vi.hasNext())
            {
                for(Allele a: vi.next().getAlleles())
                {
                    if(a.getAlleleAsString().equalsIgnoreCase(b.Alt)){
                        sampleToAntigens.get(sampleNames.get(idx)).add(b.Assoc_antigen_or_pheno);
                        break;
                    }
                }
                idx++;
            }
        }

        for(String sample : sampleToAntigens.keySet())
        {
            StringBuffer sb = new StringBuffer();
            for(String antigen : sampleToAntigens.get(sample))
            {
                if(!antigen.equalsIgnoreCase("Wild-type")){
                    sb.append(antigen + ", ");
                }
            }
            sb.delete(sb.length()-2, sb.length());
            System.out.println(sample +" has antigens: " + sb.toString());
        }

    }


}
