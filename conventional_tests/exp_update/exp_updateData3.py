__author__ = 'fyu'

from config import *
from prepare import updateData as ud
from prepare import prepareDataStringFile as pd
import numpy as np
import sys

def reject_outliers(data, m=2):
    u = np.median(data)
    s = np.std(data)
    filtered = [e for e in data if (u - m * s < e < u + m * s)]
    return filtered

# receive arguments from stdin

if(len(sys.argv)>=3):
    sysarray=sys.argv
    num_lines=int(sys.argv[1])
    max_exp_times=int(sys.argv[2])
    pers=[]
    for per in sys.argv[3:]:
        pers.append(float(per))

exp_start_time=time.time()

bat_update_time_table=np.zeros((len(pers),max_exp_times))
tbat_update_time_table=np.zeros((len(pers),max_exp_times))
bat_update_time_medians=[]
tbat_update_time_medians=[]
bat_update_time_means=[]
tbat_update_time_means=[]
bat_update_time_maxs=[]
tbat_update_time_maxs=[]
bat_update_time_mins=[]
tbat_update_time_mins=[]
overhead_medians=[]
overhead_means=[]


# ---- open result file----
result_file=open(result_file_name,'w')
result_file.write(('Total Lines: %d\n'%num_lines))
print 'Total Lines: %d\n' % num_lines

for per in pers:
    result_file.write(('percentage = %g starts\n' % per))
    print 'percentage = %g starts\n' % per

    index=0

    # initialize times
    bat_update_time=0.0
    tbat_update_time=0.0

    for t in xrange(0,max_exp_times):
        print 'loop=%d' %(t+1)
        sys.stdout.flush() # flush print in nohup.out

        # create data
        pd.prepareData(num_lines,bat_file_name,tbat_file_name)

        # update data list
        update_file_name=data_dir+'update'+str(per)+suffix
        pd.prepareUpdateList(per,num_lines,update_file_name)

        # update TBAT
        start1=time.time()
        ud.updateTBAT(tbat_file_name,update_file_name)
        temp1=time.time()-start1
        tbat_update_time_table[index][t]=temp1

        # update BAT
        start2=time.time()
        ud.updateBAT1(bat_file_name,update_file_name)
        temp2=time.time()-start2
        bat_update_time_table[index][t]=temp2
        result_file.write('loop = %3d: | tbat_time | %12g | bat_time | %12g | overhead | %12g \n'
                          % (t+1, temp1,temp2,temp2/temp1))



    # remove outlier
    tbat_update_time_cleaned=reject_outliers(tbat_update_time_table[index])
    bat_update_time_cleaned=reject_outliers(bat_update_time_table[index])

    result_file.write('after outlier cleaned times:\n')
    result_file.write('tbat times: %s\n' %(tbat_update_time_cleaned))
    result_file.write('bat times: %s\n' %(bat_update_time_cleaned))
    result_file.write('\n')

    # calculate tbat_times
    tbat_update_time_medians.append(np.median(tbat_update_time_cleaned))
    tbat_update_time_means.append(np.mean(tbat_update_time_cleaned))
    tbat_update_time_maxs.append(np.max(tbat_update_time_cleaned))
    tbat_update_time_mins.append(np.min(tbat_update_time_cleaned))

    # calculate bat times
    bat_update_time_medians.append(np.median(bat_update_time_cleaned))
    bat_update_time_means.append(np.mean(bat_update_time_cleaned))
    bat_update_time_maxs.append(np.max(bat_update_time_cleaned))
    bat_update_time_mins.append(np.min(bat_update_time_cleaned))

    overhead_medians.append(bat_update_time_medians[-1]/tbat_update_time_medians[-1])
    overhead_means.append(bat_update_time_means[-1]/tbat_update_time_means[-1])

    index+=1

result_file.write('\n')

result_file.write('update time tbat min | tbat_max | bat min | bat max:\n')
for i in xrange(0, len(pers)):
    per=pers[i]
    str='%g | %-15g | %-15g | %-15g | %-15g \n' \
        % (per, tbat_update_time_mins[i], tbat_update_time_maxs[i],\
        bat_update_time_mins[i], bat_update_time_maxs[i])
    result_file.write(str)
result_file.write('\n')

result_file.write('update time tbat median| bat median| median overhead:\n')
for i in xrange(0, len(pers)):
    per=pers[i]
    str='%g | %-15g | %-15g | %-15g \n' % (per, tbat_update_time_medians[i],bat_update_time_medians[i],overhead_medians[i])
    result_file.write(str)
result_file.write('\n')

result_file.write('update time tbat mean| bat mean| mean overhead:\n')
for i in xrange(0, len(pers)):
    per=pers[i]
    str='%g | %-15g | %-15g | %-15g \n' % (per, tbat_update_time_means[i],bat_update_time_means[i],overhead_means[i])
    result_file.write(str)
result_file.write('\n')


#--------calculate total execution time------------
exp_total_time=time.time()-exp_start_time
result_file.write('Experiment completed in %gs\n' % (exp_total_time))
print '\nExperiment completed in %gs\n' % (exp_total_time)
result_file.close()