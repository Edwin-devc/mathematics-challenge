<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Representative extends Model
{
    use HasFactory;
    protected $table = 'representatives';
    protected $primaryKey = 'representative_id';
    public $incrementing = false;
    protected $keyType = 'string';
    
    protected $fillable = [
        'representative_id',
        'name',
        'email',
        'password',
        'school_id'
    ];
}
